package com.dracoon.sdk.internal;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.dracoon.sdk.Log;
import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.model.ApiDownloadToken;
import com.dracoon.sdk.internal.model.ApiNode;
import com.dracoon.sdk.internal.util.FileUtils;
import com.dracoon.sdk.model.FileDownloadStream;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;

public class StreamDownload extends FileDownloadStream {

    private static final String LOG_TAG = StreamDownload.class.getSimpleName();

    private final DracoonClientImpl mClient;
    private final Log mLog;
    private final DracoonService mRestService;
    private final OkHttpClient mHttpClient;
    private final HttpHelper mHttpHelper;
    private final int mChunkSize;
    private final DracoonErrorParser mErrorParser;

    private final long mNodeId;

    private long mDownloadLength;
    private String mDownloadUrl;

    private byte[] mChunk;
    private boolean mLoadChunk = true;
    private long mChunkNum = 0L;
    private int mChunkOffset = 0;
    private boolean mIsClosed = false;

    StreamDownload(DracoonClientImpl client, long nodeId) throws DracoonNetIOException,
            DracoonApiException {
        mClient = client;
        mLog = client.getLog();
        mRestService = client.getDracoonService();
        mHttpClient = client.getHttpClient();
        mHttpHelper = client.getHttpHelper();
        mChunkSize = client.getHttpConfig().getChunkSize() * DracoonConstants.KIB;
        mErrorParser = client.getDracoonErrorParser();

        mNodeId = nodeId;

        mChunk = new byte[mChunkSize];

        init();
    }

    private void init() throws DracoonNetIOException, DracoonApiException {
        mDownloadLength = getFileSize();
        mDownloadUrl = createDownload();
    }

    // --- Stream methods ---

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        read(b);
        return b[0];
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        assertNotClosed();

        // If start offset and/or maximum number of bytes is invalid: Throw error
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }

        // If no bytes should be read: Abort
        if (len == 0) {
            return 0;
        }

        // Calculate total offset and total remaining bytes
        long totalOffset = mChunkNum * mChunkSize + mChunkOffset;
        long totalRemaining = mDownloadLength - totalOffset;

        // If no bytes are available anymore: Abort
        if (totalRemaining == 0) {
            return -1;
        }

        // Read while maximum number of bytes is reached
        int read = 0;
        while (read < len) {
            // Calculate current total offset and total remaining bytes
            totalOffset = mChunkNum * mChunkSize + mChunkOffset;
            totalRemaining = mDownloadLength - totalOffset;

            // If last chunk was processed: Abort
            if (totalOffset >= mDownloadLength) {
                break;
            }

            // If end of current chunk was reached: Force to load next chunk
            if (mChunkOffset == mChunkSize) {
                mLoadChunk = true;
                mChunkNum++;
                mChunkOffset = 0;
            }

            // If next chunk must be loaded: Load next chunk
            if (mLoadChunk) {
                try {
                    loadNextChunk();
                } catch (DracoonException e) {
                    throw new IOException("Could not read from download stream.", e);
                }
            }

            // Calculate number of bytes which should be copied
            int count = len - read;
            if (count > mChunkSize - mChunkOffset) {
                count = mChunkSize - mChunkOffset;
            }
            if (count > totalRemaining) {
                count = (int) totalRemaining;
            }

            mLog.d(LOG_TAG, String.format("Loading: %d: %d-%d=%d (%d-%d/%d)", mChunkNum,
                    mChunkOffset, mChunkOffset + count, count,
                    totalOffset, totalOffset + count, mDownloadLength));

            // Copy bytes
            System.arraycopy(mChunk, mChunkOffset, b, off + read, count);

            // Update chunk offset
            mChunkOffset = mChunkOffset + count;

            // Update read count
            read = read + count;
        }

        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        assertNotClosed();

        // If number of bytes to be skipped is negative: Abort
        if (n <= 0) {
            return 0;
        }

        // Calculate current total offset, total remaining bytes and skipped bytes
        long curTotalOffset = mChunkNum * mChunkSize + mChunkOffset;
        long totalRemaining = mDownloadLength - curTotalOffset;
        long skip = n < totalRemaining ? n : totalRemaining;

        // Calculate new total offset
        long newTotalOffset = curTotalOffset + skip;

        // Calculate new chunk number and new chunk offset
        long newChunkNum = newTotalOffset / mChunkSize;
        int newChunkOffset = (int) (newTotalOffset % mChunkSize);

        // If there are more bytes and a new chunk was reached: Force to load next chunk
        if (totalRemaining > skip && newChunkNum > mChunkNum) {
            mLoadChunk = true;
            mChunkNum = newChunkNum;
        }

        // Update chunk offset
        mChunkOffset = newChunkOffset;

        return skip;
    }

    @Override
    public int available() throws IOException {
        assertNotClosed();

        long offset = mChunkNum * mChunkSize + mChunkOffset;
        long remaining = mDownloadLength - offset;
        return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remaining;
    }

    @Override
    public void close() throws IOException {
        assertNotClosed();
        mChunk = null;
        mIsClosed = true;
    }

    // --- Helper methods ---

    private long getFileSize() throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();

        Call<ApiNode> call = mRestService.getNode(auth, mNodeId);
        Response<ApiNode> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseNodesQueryError(response);
            String errorText = String.format("Creation of download stream for file '%d' failed " +
                    "with '%s'!", mNodeId, errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        ApiNode node = response.body();

        return node.size;
    }

    private String createDownload() throws DracoonNetIOException, DracoonApiException {
        String auth = mClient.buildAuthString();

        Call<ApiDownloadToken> call = mRestService.getDownloadToken(auth, mNodeId);
        Response<ApiDownloadToken> response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadTokenGetError(response);
            String errorText = String.format("Creation of download stream for file '%d' failed " +
                    "with '%s'!", mNodeId, errorCode.name());
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

    private void loadNextChunk() throws DracoonNetIOException, DracoonApiException {
        long offset = mChunkNum * mChunkSize;
        long remaining = mDownloadLength - offset;
        int count = remaining > mChunkSize ? mChunkSize : (int) remaining;

        String range = "bytes=" + offset + "-" + (offset + count - 1);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(mDownloadUrl)
                .addHeader("Range", range)
                .build();

        okhttp3.Call call = mHttpClient.newCall(request);
        okhttp3.Response response = mHttpHelper.executeRequest(call);

        if (!response.isSuccessful()) {
            DracoonApiCode errorCode = mErrorParser.parseDownloadError(response);
            String errorText = String.format("Download of file '%d' failed with '%s'!", mNodeId,
                    errorCode.name());
            mLog.d(LOG_TAG, errorText);
            throw new DracoonApiException(errorCode);
        }

        BufferedInputStream is = new BufferedInputStream(response.body().byteStream());
        int bytesRead = 0;
        int bytesRemaining = mChunkSize;
        int bytesCount;
        try {
            do {
                bytesCount = is.read(mChunk, bytesRead, bytesRemaining);
                bytesRead = bytesRead + bytesCount;
                bytesRemaining = bytesRemaining - bytesCount;
            } while (bytesRemaining > 0 && bytesCount > -1);
        } catch (IOException e) {
            String errorText = "Server communication failed!";
            mLog.d(LOG_TAG, errorText);
            throw new DracoonNetIOException(errorText, e);
        } finally {
            FileUtils.closeStream(is);
        }

        mLoadChunk = false;
    }

    private void assertNotClosed() throws IOException {
        if (mIsClosed) {
            throw new IOException("Download stream was already closed.");
        }
    }

}
