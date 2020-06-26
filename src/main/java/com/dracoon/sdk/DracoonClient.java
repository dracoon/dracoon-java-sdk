package com.dracoon.sdk;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.error.DracoonCryptoException;
import com.dracoon.sdk.error.DracoonFileIOException;
import com.dracoon.sdk.error.DracoonNetIOException;
import com.dracoon.sdk.filter.GetDownloadSharesFilter;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.GetUploadSharesFilter;
import com.dracoon.sdk.filter.SearchNodesFilters;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.internal.validator.ValidatorUtils;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateDownloadShareRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.CreateUploadShareRequest;
import com.dracoon.sdk.model.CryptoAlgorithm;
import com.dracoon.sdk.model.CustomerAccount;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.DownloadShare;
import com.dracoon.sdk.model.DownloadShareList;
import com.dracoon.sdk.model.FileDownloadCallback;
import com.dracoon.sdk.model.FileDownloadStream;
import com.dracoon.sdk.model.FileUploadCallback;
import com.dracoon.sdk.model.FileUploadRequest;
import com.dracoon.sdk.model.FileUploadStream;
import com.dracoon.sdk.model.MoveNodesRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
import com.dracoon.sdk.model.UploadShare;
import com.dracoon.sdk.model.UploadShareList;
import com.dracoon.sdk.model.UserAccount;

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
 * New client instances can be created via {@link Builder}.
 */
@SuppressWarnings("unused")
public abstract class DracoonClient {

    /**
     * Available crypto versions.
     */
    public interface CryptoVersions {
        String A = "A";
        String RSA4096_AES256GCM = "RSA-4096/AES-256-GCM";
    }

    /**
     * Handler to query server information.
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

        /**
         * Get ServerSettings handler.
         *
         * @return ServerSettings handler
         */
        ServerSettings settings();

    }

    /**
     * Handler to query server settings information.
     */
    public interface ServerSettings {

        /**
         * Retrieves the server's general settings.
         *
         * @return server general settings
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        ServerGeneralSettings getGeneralSettings() throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves the server's defaults.
         *
         * @return server defaults
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        ServerDefaults getDefaults() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves the list of available crypto algorithms.
         *
         * @return available crypto algorithms
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        List<CryptoAlgorithm> getAvailableCryptoAlgorithms() throws DracoonNetIOException,
                DracoonApiException;

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
         * Retrieves a list of crypto version for which the user has encryption key pairs.
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        List<String> getUserKeyPairCryptoVersions() throws DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Sets the user's encryption key pair.
         *
         * @param cryptoVersion The crypto version for which to set a key pair.
         *
         * @throws DracoonCryptoException If a crypto error occurred at creation of the key pair.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void setUserKeyPair(String cryptoVersion) throws DracoonCryptoException, DracoonNetIOException,
                DracoonApiException;

        /**
         * Deletes the user's encryption key pair.
         *
         * @param cryptoVersion The crypto version for which to delete the key pair.
         *
         * @throws DracoonCryptoException If a crypto error occurred at the key pair check.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void deleteUserKeyPair(String cryptoVersion) throws DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Checks if the user's encryption key pair can be unlocked with the currently set
         * encryption password.
         *
         * @param cryptoVersion The crypto version for which to check the key pair password.
         *
         * @return <code>true</code> if key pair could be unlocked; <code>false</code> otherwise
         *
         * @throws DracoonCryptoException If a crypto error occurred at the key pair check.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        boolean checkUserKeyPairPassword(String cryptoVersion) throws DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Checks if the user's encryption key pair can be unlocked with the provided encryption
         * password.
         *
         * @param cryptoVersion      The crypto version for which to check the key pair password.
         * @param encryptionPassword The encryption password.
         *
         * @return <code>true</code> if key pair could be unlocked; <code>false</code> otherwise
         *
         * @throws DracoonCryptoException If a crypto error occurred at the key pair check.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        boolean checkUserKeyPairPassword(String cryptoVersion, String encryptionPassword)
                throws DracoonCryptoException, DracoonNetIOException, DracoonApiException;

    }

    /**
     * Handler to maintain users.
     */
    public interface Users {

    }

    /**
     * Handler to maintain groups.
     */
    public interface Groups {

    }

    /**
     * Handler to perform node actions.
     */
    public interface Nodes {

        /**
         * Retrieves child nodes of a node.<br>
         * <br>
         * Use parent node ID 0 to retrieve root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getNodes(long parentNodeId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves child nodes of a node.<br>
         * <br>
         * Use parent node ID 0 to retrieve root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param filters      The filters to apply.
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getNodes(long parentNodeId, GetNodesFilters filters) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves child nodes of a node. The arguments {@code offset} and {@code limit} restrict
         * the result to a specific range.<br>
         * <br>
         * Use parent node ID 0 to retrieve root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getNodes(long parentNodeId, long offset, long limit)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves child nodes of a node. The arguments {@code offset} and {@code limit} restrict
         * the result to a specific range.<br>
         * <br>
         * Use parent node ID 0 to retrieve root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param filters      The filters to apply.
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getNodes(long parentNodeId, GetNodesFilters filters, long offset, long limit)
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
         * Retrieves a node.
         *
         * @param path The path of the node.
         *
         * @return the node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node getNode(String path) throws DracoonNetIOException, DracoonApiException;

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
        Node createRoom(CreateRoomRequest request) throws DracoonNetIOException,
                DracoonApiException;

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
        Node updateRoom(UpdateRoomRequest request) throws DracoonNetIOException,
                DracoonApiException;

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
         * Copies nodes.
         *
         * @param request The request with target node ID and IDs of nodes which should be copied.
         *
         * @return the updated target node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node copyNodes(CopyNodesRequest request) throws DracoonNetIOException, DracoonApiException;

        /**
         * Moves nodes.
         *
         * @param request The request with target node ID and IDs of nodes which should be moved.
         *
         * @return the updated target node
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        Node moveNodes(MoveNodesRequest request) throws DracoonNetIOException, DracoonApiException;

        /**
         * Uploads a file.
         *
         * @param id       The ID for the upload. (This ID can be used to keep a reference.)
         * @param request  The request with information about the file.
         * @param file     The source file.
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @return the new node
         *
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        Node uploadFile(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Uploads a file.
         *
         * @param id       The ID for the upload. (This ID can be used to keep a reference.)
         * @param request  The request with information about the file.
         * @param is       The source stream.
         * @param length   The length of the upload. (<code>0</code> if not known.)
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @return the new node
         *
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        Node uploadFile(String id, FileUploadRequest request, InputStream is, long length,
                FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Starts an asynchronous file upload.
         *
         * @param id       The ID for the upload. (This ID can be used to keep a reference.)
         * @param request  The request with information about the file.
         * @param file     The source file.
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonFileIOException If a file error occurred.
         * @throws DracoonCryptoException If the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void startUploadFileAsync(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonFileIOException, DracoonCryptoException,
                DracoonNetIOException, DracoonApiException;

        /**
         * Starts an asynchronous file upload.
         *
         * @param id       The ID for the upload. (This ID can be used to keep a reference.)
         * @param request  The request with information about the file.
         * @param is       The source stream.
         * @param length   The length of the upload. (<code>0</code> if not known.)
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonCryptoException If the encryption failed.
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         */
        void startUploadFileAsync(String id, FileUploadRequest request, InputStream is, long length,
                FileUploadCallback callback) throws DracoonCryptoException, DracoonNetIOException,
                DracoonApiException;

        /**
         * Cancels an asynchronous file upload.
         *
         * @param id The ID of the upload.
         */
        void cancelUploadFileAsync(String id);

        /**
         * Creates a file upload stream.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param request  The request with information about the file.
         * @param length   The length of the upload. (<code>0</code> if not known.)
         * @param callback A callback which get called when the upload was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @return output stream for upload
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the encryption failed.
         */
        FileUploadStream createFileUploadStream(String id, FileUploadRequest request, long length,
                FileUploadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException;

        /**
         * Downloads a file.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId   The ID of the node.
         * @param file     The target file.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the decryption failed.
         * @throws DracoonFileIOException If a file error occurred.
         */
        void downloadFile(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException, DracoonFileIOException;

        /**
         * Downloads a file.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId   The ID of the node.
         * @param os       The target stream.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the decryption failed.
         * @throws DracoonFileIOException If a file error occurred.
         */
        void downloadFile(String id, long nodeId, OutputStream os,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException, DracoonFileIOException;

        /**
         * Starts an asynchronous file download.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId   The ID of the node.
         * @param file     The target file.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the decryption failed.
         * @throws DracoonFileIOException If a file error occurred.
         */
        void startDownloadFileAsync(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException, DracoonFileIOException;

        /**
         * Starts an asynchronous file download.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId   The ID of the node.
         * @param os       The target stream.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the decryption failed.
         */
        void startDownloadFileAsync(String id, long nodeId, OutputStream os,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException;

        /**
         * Cancels an asynchronous file download.
         *
         * @param id The ID of the download.
         */
        void cancelDownloadFileAsync(String id);

        /**
         * Creates a file download stream.
         *
         * @param id       The ID for the download. (This ID can be used to keep a reference.)
         * @param nodeId   The ID of the node.
         * @param callback A callback which get called when the download was started, finished and
         *                 so on. (<code>null</code>, if not needed.)
         *
         * @return input stream for download
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If the decryption failed.
         */
        FileDownloadStream createFileDownloadStream(String id, long nodeId,
                FileDownloadCallback callback) throws DracoonNetIOException, DracoonApiException,
                DracoonCryptoException;

        /**
         * Searches child nodes of a node by their name.<br>
         * <br>
         * Use parent node ID <code>0</code> to search in all root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param searchString The search string. (Search string must not be empty.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList searchNodes(long parentNodeId, String searchString)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Searches child nodes of a node by their name.<br>
         * <br>
         * Use parent node ID <code>0</code> to search in all root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param searchString The search string. (Search string must not be empty.)
         * @param filters      The filters to apply.
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList searchNodes(long parentNodeId, String searchString, SearchNodesFilters filters)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Searches child nodes of a node by their name. The arguments {@code offset} and
         * {@code limit} restrict the result to a specific range.<br>
         * <br>
         * Use parent node ID <code>0</code> to search in all root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param searchString The search string. (Search string must not be empty.)
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList searchNodes(long parentNodeId, String searchString, long offset, long limit)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Searches child nodes of a node by their name. The arguments {@code offset} and
         * {@code limit} restrict the result to a specific range.<br>
         * <br>
         * Use parent node ID <code>0</code> to search in all root nodes.
         *
         * @param parentNodeId The ID of the parent node. (ID must be 0 or positive.)
         * @param searchString The search string. (Search string must not be empty.)
         * @param filters      The filters to apply.
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of nodes
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList searchNodes(long parentNodeId, String searchString, SearchNodesFilters filters,
                long offset, long limit) throws DracoonNetIOException, DracoonApiException;

        /**
         * Generates file keys for files with missing file keys. The argument {@code limit}
         * restricts the generation to a certain number.
         *
         * @param cryptoVersion The crypto version for which to generates file keys.
         * @param limit         The number limit. (Number of records; must be positive.)
         *
         * @return <code>true</code> if all file keys have been generated and no file keys are
         *         missing anymore; <code>false</code> otherwise
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If a encryption/decryption failed.
         */
        boolean generateMissingFileKeys(String cryptoVersion, int limit)
                throws DracoonNetIOException, DracoonApiException, DracoonCryptoException;

        /**
         * Generates file keys for files with missing file keys. The argument {@code limit}
         * restricts the generation to a certain number.
         *
         * @param cryptoVersion The crypto version for which to generates file keys.
         * @param nodeId        The node ID of the file.
         * @param limit         The number limit. (Number of records; must be positive.)
         *
         * @return <code>true</code> if all file keys have been generated and no file keys are
         *         missing anymore; <code>false</code> otherwise
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If a encryption/decryption failed.
         */
        boolean generateMissingFileKeys(String cryptoVersion, long nodeId, int limit)
                throws DracoonNetIOException, DracoonApiException, DracoonCryptoException;

        /**
         * Marks a node as a favorite.
         *
         * @param nodeId The ID of the node.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void markFavorite(long nodeId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Unmarks a node as a favorite.
         *
         * @param nodeId The ID of the node.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void unmarkFavorite(long nodeId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves favorites.
         *
         * @return list of favorites
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getFavorites() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves favorites. The arguments {@code offset} and {@code limit} restrict the result
         * to a specific range.
         *
         * @param offset The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit  The range limit. (Number of records; must be positive.)
         *
         * @return list of favorites
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        NodeList getFavorites(long offset, long limit) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Builds a media URL. The URL can be used to get a thumbnail or preview image for a node.
         *
         * @param mediaToken The media token for the node.
         * @param width      The width of the image. (Must positive.)
         * @param height     The height of the image. (Must positive.)
         *
         * @return the media URL
         */
        URL buildMediaUrl(String mediaToken, int width, int height);

    }

    /**
     * Handler to maintain shares.
     */
    public interface Shares {

        /**
         * Creates a download share.
         *
         * @param request The request with the node ID and the download share settings.
         *
         * @return the download share
         *
         * @throws DracoonNetIOException  If a network error occurred.
         * @throws DracoonApiException    If the API responded with an error.
         * @throws DracoonCryptoException If a crypto error occurred at creation of the key pair or
         *                                the file key.
         */
        DownloadShare createDownloadShare(CreateDownloadShareRequest request)
                throws DracoonNetIOException, DracoonApiException, DracoonCryptoException;

        /**
         * Retrieves all download shares.
         *
         * @return list of download shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        DownloadShareList getDownloadShares() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves all download shares. The arguments {@code offset} and {@code limit} restrict
         * the result to a specific range.
         *
         * @param offset The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit  The range limit. (Number of records; must be positive.)
         *
         * @return list of download shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        DownloadShareList getDownloadShares(long offset, long limit) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves filtered download shares.
         *
         * @param filters The filters to apply.
         *
         * @return list of download shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        DownloadShareList getDownloadShares(GetDownloadSharesFilter filters)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves filtered download shares. The arguments {@code offset} and {@code limit}
         * restrict the result to a specific range.
         *
         * @param filters The filters to apply.
         * @param offset  The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit   The range limit. (Number of records; must be positive.)
         *
         * @return list of download shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        DownloadShareList getDownloadShares(GetDownloadSharesFilter filters, long offset,
                long limit) throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieve the QR code image for a given download share ID as byte array.
         *
         * @param shareId The download share ID.
         *
         * @return the byte array with the qr image
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        byte[] getDownloadShareQrCode(long shareId) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Delete a download share.
         *
         * @param shareId The ID of the download share which should be deleted.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void deleteDownloadShare(long shareId) throws DracoonNetIOException, DracoonApiException;

        /**
         * Creates an upload share.
         *
         * @param request The request with the target node ID and the upload share settings.
         *
         * @return the upload share
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UploadShare createUploadShare(CreateUploadShareRequest request)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves all upload shares.
         *
         * @return list of upload shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UploadShareList getUploadShares() throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieves all upload shares. The arguments {@code offset} and {@code limit} restrict
         * the result to a specific range.
         *
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of upload shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UploadShareList getUploadShares(long offset, long limit) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves filtered upload shares.
         *
         * @param filters      The filters to apply.
         *
         * @return list of upload shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UploadShareList getUploadShares(GetUploadSharesFilter filters) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Retrieves filtered upload shares. The arguments {@code offset} and {@code limit}
         * restrict the result to a specific range.
         *
         * @param filters      The filters to apply.
         * @param offset       The range offset. (Zero-based index; must be 0 or positive.)
         * @param limit        The range limit. (Number of records; must be positive.)
         *
         * @return list of upload shares
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        UploadShareList getUploadShares(GetUploadSharesFilter filters, long offset, long limit)
                throws DracoonNetIOException, DracoonApiException;

        /**
         * Retrieve the QR code image for a given upload share ID as byte array.
         *
         * @param shareId The upload share ID.
         *
         * @return the byte array with the QR code image
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        byte[] getUploadShareQrCode(long shareId) throws DracoonNetIOException,
                DracoonApiException;

        /**
         * Delete a upload share.
         *
         * @param shareId The ID of the upload share which should be deleted.
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        void deleteUploadShare(long shareId) throws DracoonNetIOException, DracoonApiException;

    }

    protected URL mServerUrl;

    /**
     * Constructs a new Dracoon client.
     *
     * @param serverUrl The URL of the Dracoon server.
     */
    protected DracoonClient(URL serverUrl) {
        mServerUrl = serverUrl;
    }

    /**
     * Returns the client's server URL.
     *
     * @return server URL
     */
    public URL getServerUrl() {
        return mServerUrl;
    }

    /**
     * Returns the <b>current</b> authorization data.<br>
     * <br>
     * This method can be used to get the current access and refresh tokens after the SDK has
     * retrieved them with an authorization code or refreshed them with an previous refresh token.
     *
     * @return authorization data
     */
    public abstract DracoonAuth getAuth();

    /**
     * Checks if the <b>current</b> authorization is still valid.
     *
     * @return <code>true</code> if authorization is still valid; <code>false</code> otherwise
     *
     * @throws DracoonNetIOException If a network error occurred.
     * @throws DracoonApiException   If the API responded with an error.
     */
    public abstract boolean checkAuth() throws DracoonNetIOException, DracoonApiException;

    /**
     * Returns the client's encryption password.
     *
     * @return password
     */
    public abstract String getEncryptionPassword();

    /**
     * Sets the client's encryption password.
     *
     * @param encryptionPassword The password.
     */
    public abstract void setEncryptionPassword(String encryptionPassword);

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
     * - Server URL (mandatory): {@link #Builder(URL)}<br>
     * - Logger:                 {@link #log(Log)}<br>
     * - Authorization data:     {@link #auth(DracoonAuth)}<br>
     * - Encryption password:    {@link #encryptionPassword(String)}<br>
     * - HTTP configuration:     {@link #httpConfig(DracoonHttpConfig)}
     */
    public static class Builder {

        private DracoonClientImpl mClient;
        private DracoonHttpConfig mHttpConfig;

        /**
         * Constructs a new builder for a specific Dracoon server.
         *
         * @param serverUrl The URL of the Dracoon server.
         */
        public Builder(URL serverUrl) {
            ValidatorUtils.validateServerURL(serverUrl);
            mClient = new DracoonClientImpl(serverUrl);
            mHttpConfig = new DracoonHttpConfig();
        }

        /**
         * Sets the authorization data for accessing protected resources.
         *
         * @param auth The authorization data.
         *
         * @return a reference to this object
         */
        public Builder auth(DracoonAuth auth) {
            mClient.setAuth(auth);
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
            mClient.setEncryptionPassword(encryptionPassword);
            return this;
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
         * Sets the HTTP configuration.
         *
         * @param httpConfig The HTTP configuration.
         *
         * @return a reference to this object
         */
        public Builder httpConfig(DracoonHttpConfig httpConfig) {
            mHttpConfig = httpConfig;
            return this;
        }

        /**
         * Creates a new {@link DracoonClient} instance with the supplied configuration.<br>
         * <br>
         * Beside creating a new instance, this methods does some pre-flight checks. It checks if
         * the server API version is supported by the SDK. Furthermore, if authorization data was
         * provided, new OAuth tokens are retrieved and a authorization check is made. (Afterwards,
         * the current authorization data can be retrieved via {@link DracoonClient#getAuth()}.)
         *
         * @return a new {@link DracoonClient} instance
         *
         * @throws DracoonNetIOException If a network error occurred.
         * @throws DracoonApiException   If the API responded with an error.
         */
        public DracoonClient build() throws DracoonNetIOException, DracoonApiException {
            mClient.setHttpConfig(mHttpConfig);
            mClient.init();
            return mClient;
        }

    }

}
