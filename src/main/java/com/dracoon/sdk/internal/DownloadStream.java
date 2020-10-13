package com.dracoon.sdk.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.crypto.Crypto;
import com.dracoon.sdk.crypto.CryptoUtils;
import com.dracoon.sdk.crypto.error.BadFileException;
import com.dracoon.sdk.crypto.error.CryptoException;
import com.dracoon.sdk.crypto.error.CryptoSystemException;
import com.dracoon.sdk.crypto.FileDecryptionCipher;
import com.dracoon.sdk.crypto.model.EncryptedDataContainer;
import com.dracoon.sdk.crypto.model.PlainDataContainer;
import com.dracoon.sdk.crypto.model.PlainFileKey;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoCode;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.util.StreamUtils;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileDownloadStream;
import okhttp3.OkHttpClient;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadStream extends FileDownloadStream {

    private static final String LOG_TAG = DownloadStream.class.getSimpleName();

    private static final int BLOCK_SIZE = 2 * DracoonConstants.KIB;
    private static final long PROGRESS_UPDATE_INTERVAL = 100;

    private final DracoonClientImpl mClient;
    private final Log mLog;
    private final DracoonService mRestService;
    private final OkHttpClient mHttpClient;
    private final HttpHelper mHttpHelper;
    private final DracoonErrorParser mErrorParser;

    private final String mId;
    private final long mNodeId;
    private final PlainFileKey mFileKey;

    private FileDecryptionCipher mDecryptionCipher;

    private String mDownloadUrl;
    private long mDownloadOffset = 0L;
    private long mDownloadLength;

    private Buffer mDownloadBuffer = new Buffer();
    private InputStream mDownloadInputStream = null;

    private int mChunkSize;
    private int mChunkNum = 0;
    private int mChunkOffset = 0;
    private boolean mRequestNextChunk = true;

    private boolean mIsClosed = false;

    private Thread mThread;

    private long mProgressUpdateTime = System.currentTimeMillis();

    private final List<FileDownloadCallback> mCallbacks = new ArrayList<>();

    DownloadStream(DracoonClientImpl client, String id, long nodeId, PlainFileKey fileKey) {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpClient = client.getHttpClient();
        mHttpHelper = client.getHttpHelper();
        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
        mErrorParser = client.getDracoonErrorParser();

        mId = id;
        mNodeId = nodeId;
        mFileKey = fileKey;
    }

    void start() throws DracoonNetIOException, DracoonApiException, DracoonCryptoException {
        mThread = Thread.currentThread();

        try {
            notifyStarted(mId);

            if (isEncryptedDownload()) {
                mDecryptionCipher = createDecryptionCipher();
            }

            mDownloadUrl = createDownload();
            mDownloadLength = getFileSize();
        } catch (InterruptedException e) {
            notifyCanceled(mId);
        } catch (DracoonException e) {
            notifyFailed(mId, e);
            throw e;
        }
    }

    private boolean isEncryptedDownload() {
        return mFileKey != null;
    }

    public void addCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(FileDownloadCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    // --- Stream methods ---

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        read(b);
        return b[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        assertNotClosed();

        // If start offset and/or maximum number of bytes is invalid: Throw error
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }

        // If no bytes should be read: Abort
        if (len == 0) {
            return 0;
        }

        int read = -1;

        int bOff = off;
        int bLen = len;

        // Read from buffer while requested length was read
        while (bLen > 0) {
            int bRead = mDownloadBuffer.read(b, bOff, bLen);
            // If buffer is exhausted: Try to download more data
            if (bRead < 0) {
                // Try to download more data
                boolean more;
                try {
                    more = downloadData();
                } catch (InterruptedException e) {
                    notifyCanceled(mId);
                    return -1;
                } catch (DracoonException e) {
                    notifyFailed(mId, e);
                    throw new IOException("Could not read from download stream.", e);
                }
                // If more data is available: Continue
                if (more) {
                    continue;
                // Otherwise: Abort
                } else {
                    break;
                }
            }
            bOff = bOff + bRead;
            bLen = bLen - bRead;
            read = read >= 0 ? read + bRead : bRead;
        }

        if (read == -1) {
            notifyFinished(mId);
        }

        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        assertNotClosed();

        // If number of bytes to be skipped is negative: Abort
        if (n <= 0L) {
            return 0L;
        }

        // TODO: Add logic to skip bytes
        return 0L;
    }

    @Override
    public int available() throws IOException {
        assertNotClosed();

        long remaining = mDownloadLength - mDownloadOffset;
        return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remaining;
    }

    @Override
    public void close() throws IOException {
        assertNotClosed();
        StreamUtils.closeStream(mDownloadInputStream);
        mIsClosed = true;
    }

    // --- Helper methods ---

    private FileDecryptionCipher createDecryptionCipher() throws DracoonCryptoException {
        try {
            return Crypto.createFileDecryptionCipher(mFileKey);
        } catch (CryptoException e) {
            String errorText = String.format("Decryption failed at download of '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        }
    }

    private long getFileSize() throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        Call<ApiNode> call = mRestService.getNode(mNodeId);
        Response<ApiNode> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("Creation of download stream for '%s' failed with " +
                    "'%s'!", mId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode node = response.body();

        return node.size;
    }

    private String createDownload() throws DracoonNetIOException, DracoonApiException,
            InterruptedException {
        Call<ApiDownloadToken> call = mRestService.getDownloadToken(mNodeId);
        Response<ApiDownloadToken> response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadTokenGetError(response);
            String errorText = String.format("Creation of download stream for '%s' failed with " +
                    "'%s'!", mId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiDownloadToken downloadToken = response.body();

        if (downloadToken.downloadUrl != null) {
            return downloadToken.downloadUrl;
        } else {
            return mClient.buildApiUrl("downloads", downloadToken.token);
        }
    }

    private boolean downloadData() throws DracoonNetIOException, DracoonApiException,
            DracoonCryptoException, DracoonFileIOException, InterruptedException {
        if (mDownloadOffset == mDownloadLength) {
            return false;
        }

        if (mRequestNextChunk) {
            long offset = mDownloadOffset;
            long remaining = mDownloadLength - mDownloadOffset;
            long size = remaining > mChunkSize ? mChunkSize : remaining;
            mDownloadInputStream = requestNextChunk(offset, size);
            mRequestNextChunk = false;
            mChunkNum++;
            mChunkOffset = 0;
        }

        byte[] bytes = downloadBytes(mDownloadInputStream, BLOCK_SIZE);
        int count = bytes.length;
        if (count == 0) {
            mRequestNextChunk = true;
            return true;
        }

        mLog.d(LOG_TAG, String.format("Loading: id='%s': chunk=%d: %d-%d=%d (%d-%d/%d)", mId,
                mChunkNum, mChunkOffset, mChunkOffset + count - 1, count,
                mDownloadOffset, mDownloadOffset + count - 1, mDownloadLength));

        mDownloadOffset = mDownloadOffset + count;
        mChunkOffset = mChunkOffset + count;

        if (isEncryptedDownload()) {
            boolean isLastBytes = mDownloadOffset == mDownloadLength;
            bytes = decryptBytes(bytes, isLastBytes);
        }

        mDownloadBuffer.write(bytes);

        return true;
    }

    private InputStream requestNextChunk(long offset, long size) throws DracoonNetIOException,
            DracoonApiException, InterruptedException {
        String range = "bytes=" + offset + "-" + (offset + size - 1);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(mDownloadUrl)
                .addHeader("Range", range)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = mHttpHelper.executeRequest(call, mThread);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadError(response);
            String errorText = String.format("Download of '%s' failed with '%s'!", mId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        return new BufferedInputStream(response.body().byteStream());
    }

    private byte[] downloadBytes(InputStream is, int length) throws DracoonNetIOException,
            InterruptedException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[length];
        int read = 0;
        try {
            while (read < length) {
                int count = is.read(buffer, 0, length);
                if (count < 0) {
                    break;
                }

                os.write(buffer, 0, count);
                read = read + count;

                if (mProgressUpdateTime + PROGRESS_UPDATE_INTERVAL < System.currentTimeMillis()
                        && !mThread.isInterrupted()) {
                    notifyRunning(mId, mDownloadOffset + read, mDownloadLength);
                    mProgressUpdateTime = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = "Server communication failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        } finally {
            StreamUtils.closeStream(os);
        }

        return os.toByteArray();
    }

    private byte[] decryptBytes(byte[] bytes, boolean isLast) throws DracoonFileIOException,
            DracoonCryptoException, InterruptedException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        EncryptedDataContainer encData;
        PlainDataContainer plainData;
        try {
            encData = new EncryptedDataContainer(bytes, null);
            plainData = mDecryptionCipher.processBytes(encData);
            os.write(plainData.getContent());

            if (isLast) {
                byte[] encTag = CryptoUtils.stringToByteArray(mFileKey.getTag());
                encData = new EncryptedDataContainer(null, encTag);
                plainData = mDecryptionCipher.doFinal(encData);
                os.write(plainData.getContent());
            }
        } catch (BadFileException | IllegalArgumentException | IllegalStateException |
                CryptoSystemException e) {
            String errorText = String.format("Decryption failed at download of '%s'! %s", mId,
                    e.getMessage());
            mLog.d(LOG_TAG, errorText);
            DracoonCryptoCode errorCode = CryptoErrorParser.parseCause(e);
            throw new DracoonCryptoException(errorCode, e);
        } catch (IOException e) {
            if (mThread.isInterrupted()) {
                throw new InterruptedException();
            }
            String errorText = "Buffer write failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonFileIOException(errorText, e);
        } finally {
            StreamUtils.closeStream(os);
        }

        return os.toByteArray();
    }

    private void assertNotClosed() throws IOException {
        if (mIsClosed) {
            throw new IOException("Download stream was already closed.");
        }
    }

    // --- Callback helper methods ---

    private void notifyStarted(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onStarted(id);
        }
    }

    private void notifyRunning(String id, long bytesRead, long bytesTotal) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onRunning(id, bytesRead, bytesTotal);
        }
    }

    private void notifyFinished(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onFinished(id);
        }
    }

    private void notifyCanceled(String id) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onCanceled(id);
        }
    }

    private void notifyFailed(String id, DracoonException e) {
        for (FileDownloadCallback callback : mCallbacks) {
            callback.onFailed(id, e);
        }
    }

}
