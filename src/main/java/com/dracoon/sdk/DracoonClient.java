package com.dracoon.sdk;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import com.dracoon.sdk.model.UserAccount;

import java.io.File;
import java.util.Date;

/**
 * DracoonClient is the main class of the Dracoon SDK. It contains several handlers which group the
 * functions of the SDK logically.<br>
 * <br>
 * Following handlers are available:<br>
 * - {@link Server Server}:   Query server information<br>
 * - {@link Account Account}: Query user/customer account information, set/delete encryption key
 *                            pair, ...<br>
 * - {@link Users Users}:     Not implemented yet<br>
 * - {@link Groups Groups}:   Not implemented yet<br>
 * - {@link Nodes Nodes}:     Query node(s), create room/folder, update room/folder/file,
 *                            upload/download files, ...<br>
 * - {@link Shares Shares}:   Not implemented yet<br>
 * <br>
 * New client instances can be created via {@link Builder Builder}.
 */
@SuppressWarnings("unused")
public abstract class DracoonClient {

    /**
     * Handler to query server information
     */
    public interface Server {

        /**
         * Retrieves the server's version.
         *
         * @return server version
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        String getVersion() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves the server's time.
         *
         * @return server time
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Date getTime() throws DracoonNetIOException, DracoonApiException;

    }

    /**
     * Handler to query and update the user account.
     */
    public interface Account {

        /**
         * Retrieves user account information.
         *
         * @return user account information
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UserAccount getUserAccount() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves customer account information.
         *
         * @return customer account information
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        CustomerAccount getCustomerAccount() throws DracoonNetIOException, DracoonApiException;

        /**
         * Sets the user's encryption key pair.
         *
         * @throws DracoonCryptoException If a crypto error occurred at creation of the key pair.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void setUserKeyPair() throws DracoonCryptoException, DracoonNetIOException,
                DracoonApiException;

        /**
         * Checks if the user's encryption key pair can be unlocked whit the provided encryption
         * password.
         *
         * @return <code>true</code> if key pair could be unlocked; <code>false</code> otherwise
         *
         * @throws DracoonCryptoException If a crypto error occurred at the key pair check.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        boolean checkUserKeyPairPassword() throws DracoonCryptoException, DracoonNetIOException,
                DracoonApiException;

        /**
         * Deletes the user's encryption key pair.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void deleteUserKeyPair() throws DracoonNetIOException, DracoonApiException;
    }

    /**
     * Handler to maintain the users.
     */
    public interface Users {

    }

    /**
     * Handler to maintain the groups.
     */
    public interface Groups {

    }

    /**
     * Handler to perform node actions.
     */
    public interface Nodes {

        /**
         * Retrieves all root nodes.
         *
         * @return root nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getRootNodes() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves root nodes for a specific range.
         *
         * @param offset The range offset.
         * @param limit  The range limit.
         *
         * @return root nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getRootNodes(int offset, int limit) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves all child nodes of a node.
         *
         * @param parentNodeId The ID of the parent node.
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getChildNodes(long parentNodeId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves child nodes of a node for a specific range.
         *
         * @param parentNodeId The ID of the parent node.
         * @param offset       The range offset.
         * @param limit        The range limit.
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getChildNodes(long parentNodeId, int offset, int limit)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves a node.
         *
         * @param nodeId The ID of the node.
         *
         * @return the node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node getNode(long nodeId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Creates a new room.
         *
         * @param request The request with information about the new room.
         *
         * @return the new node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node createRoom(CreateRoomRequest request) throws DracoonNetIOException, DracoonApiException;

        /**
         * Updates a room.
         *
         * @param request The request with updated information about the room.
         *
         * @return the updated node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node updateRoom(UpdateRoomRequest request) throws DracoonNetIOException, DracoonApiException;

        /**
         * Creates a new folder.
         *
         * @param request The request with information about the new folder.
         *
         * @return the new node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node createFolder(CreateFolderRequest request) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Updates a folder.
         *
         * @param request The request with updated information about the folder.
         *
         * @return the updated node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node updateFolder(UpdateFolderRequest request) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Updates a file.
         *
         * @param request The request with updated information about the file.
         *
         * @return the updated node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node updateFile(UpdateFileRequest request) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Deletes nodes.
         *
         * @param request The request with IDs of nodes which should be deleted.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void deleteNodes(DeleteNodesRequest request) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Uploads a file.
         *
         * @param id A ID for the upload. (This ID can be used to keep a reference.)
         * @param request The request with information about the file.
         * @param file The source file.
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (Null, if not needed.)
         *
         * @return the new node
         *
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If a the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        Node uploadFile(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Starts an asynchronous file upload.
         *
         * @param id ID for the upload. (This ID can be used to keep a reference.)
         * @param request The request with information about the file.
         * @param file The source file.
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (Null, if not needed.)
         *
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If a the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void startUploadFileAsync(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Cancels an asynchronous file upload.
         *
         * @param id The ID of the upload.
         */
        void cancelUploadFileAsync(String id);

        /**
         * Downloads a file.
         *
         * @param id ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId The ID of the node.
         * @param file The target file.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (Null, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If a the decryption failed.
         * @throws DracoonFileIOException If a file error occurred.
         */
        void downloadFile(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException, DracoonFileIOException;

        /**
         * Starts an asynchronous file download.
         *
         * @param id ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId The ID of the node.
         * @param file The target file.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (Null, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If a the decryption failed.
         */
        void startDownloadFileAsync(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonFileIOException, DracoonCryptoException;

        /**
         * Cancels an asynchronous file download.
         *
         * @param id The ID of the download.
         */
        void cancelDownloadFileAsync(String id);
    }

    /**
     * Handler to maintain the shares.
     */
    public interface Shares {

    }

    protected String mServerUrl;

    protected String mAccessToken;
    protected String mEncryptionPassword;

    protected boolean mIsHttpRetryEnabled;
    protected int mHttpConnectTimeout;
    protected int mHttpReadTimeout;
    protected int mHttpWriteTimeout;

    /**
     * Constructs a new Dracoon client.
     *
     * @param serverUrl The URL of the Dracoon server.
     */
    protected DracoonClient(String serverUrl) {
        mServerUrl = serverUrl;
    }

    /**
     * Returns the client's server URL.
     *
     * @return server URL
     */
    public String getServerUrl() {
        return mServerUrl;
    }

    /**
     * Returns the client's access token.
     *
     * @return access token
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Sets the client's access token.
     *
     * @param accessToken The new access token.
     */
    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    /**
     * Returns the client's encryption password.
     *
     * @return encryption password
     */
    public String getEncryptionPassword() {
        return mEncryptionPassword;
    }

    /**
     * Sets the client's encryption password.
     *
     * @param encryptionPassword The new encryption password.
     */
    public void setEncryptionPassword(String encryptionPassword) {
        mEncryptionPassword = encryptionPassword;
    }

    /**
     * Get Server handler.
     *
     * @return Server handler
     */
    public abstract Server server();

    /**
     * Get Account handler.
     *
     * @return Account handler
     */
    public abstract Account account();

    /**
     * Get Users handler.
     *
     * @return Users handler
     */
    public abstract Users users();

    /**
     * Get Groups handler.
     *
     * @return Group handler
     */
    public abstract Groups groups();

    /**
     * Get Nodes handler.
     *
     * @return Nodes handler
     */
    public abstract Nodes nodes();

    /**
     * Get Shares handler.
     *
     * @return Shares handler
     */
    public abstract Shares shares();

    /**
     * This builder creates new instances of {@link DracoonClient}.<br>
     * <br>
     * Following properties can be set:<br>
     * - Server URL (mandatory): {@link Builder#Builder(String serverUrl)}<br>
     * - Access token:           {@link Builder#accessToken(String accessToken)}<br>
     * - Encryption password:    {@link Builder#encryptionPassword(String encryptionPassword)}<br>
     * - Logger:                 {@link Builder#log(Log log)}
     */
    public static class Builder {

        private DracoonClientImpl mClient;

        /**
         * Constructs a new builder for a specific Dracoon server.
         *
         * @param serverUrl The URL of the Dracoon server.
         */
        public Builder(String serverUrl) {
            mClient = new DracoonClientImpl(serverUrl);

            mClient.mIsHttpRetryEnabled = false;
            mClient.mHttpConnectTimeout = 15;
            mClient.mHttpReadTimeout = 15;
            mClient.mHttpWriteTimeout = 15;
        }

        /**
         * Sets the logger which should be used.
         *
         * @param log The logger.
         *
         * @return a reference to this object
         */
        public Builder log(Log log) {
            mClient.setLog(log);
            return this;
        }

        /**
         * Sets the access token which is used at requests.
         *
         * @param accessToken The access token.
         *
         * @return a reference to this object
         */
        public Builder accessToken(String accessToken) {
            mClient.mAccessToken = accessToken;
            return this;
        }

        /**
         * Sets the encryption password which is used at en/decryption.
         *
         * @param encryptionPassword The encryption password.
         *
         * @return a reference to this object
         */
        public Builder encryptionPassword(String encryptionPassword) {
            mClient.mEncryptionPassword = encryptionPassword;
            return this;
        }

        /**
         * Enables/disables HTTP retry. If enabled, requests which failed due to a network error
         * will be retried 2 times.<br>
         * <br>
         * The HTTP retry is disabled by default.
         *
         * @param isHttpRetryEnabled <code>true</code> to enable HTTP retry; otherwise
         *                           <code>false</code>
         *
         * @return a reference to this object
         */
        public Builder httpRetryEnabled(boolean isHttpRetryEnabled) {
            mClient.mIsHttpRetryEnabled = isHttpRetryEnabled;
            return this;
        }

        /**
         * Sets HTTP timeouts. A value of 0 means no timeout.<br>
         * <br>
         * The HTTP timeouts are set to 15 seconds by default.
         *
         * @param httpConnectTimeout The connect timeout for new connections in seconds.
         * @param httpReadTimeout    The read timeout for new connections in seconds.
         * @param httpWriteTimeout   The write timeout for new connections in seconds.
         *
         * @return a reference to this object
         */
        public Builder httpTimeout(int httpConnectTimeout, int httpReadTimeout,
                int httpWriteTimeout) {
            mClient.mHttpConnectTimeout = httpConnectTimeout;
            mClient.mHttpReadTimeout = httpReadTimeout;
            mClient.mHttpWriteTimeout = httpWriteTimeout;
            return this;
        }

        /**
         * Creates a new {@link DracoonClient} instance with the supplied configuration.
         *
         * @return a new {@link DracoonClient} instance
         */
        public DracoonClient build() {
            mClient.init();
            return mClient;
        }

    }

}
