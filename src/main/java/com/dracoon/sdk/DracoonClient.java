package com.dracoon.sdk;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
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

public abstract class DracoonClient {

    public interface Server {
        String getVersion() throws DracoonException;
        Date getTime() throws DracoonException;
    }

    public interface Account {
        UserAccount getUserAccount() throws DracoonException;
    }

    public interface Users {

    }

    public interface Groups {

    }

    public interface Roles {

    }

    public interface Permissions {

    }

    public interface Nodes {
        NodeList getRootNodes() throws DracoonException;
        NodeList getChildNodes(long parentNodeId) throws DracoonException;
        Node getNode(long nodeId) throws DracoonException;

        Node createRoom(CreateRoomRequest request) throws DracoonException;
        Node updateRoom(UpdateRoomRequest request) throws DracoonException;
        Node createFolder(CreateFolderRequest request) throws DracoonException;
        Node updateFolder(UpdateFolderRequest request) throws DracoonException;
        Node updateFile(UpdateFileRequest request) throws DracoonException;
        void deleteNodes(DeleteNodesRequest request) throws DracoonException;

        Node uploadFile(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonException;
        void startUploadFileAsync(String id, FileUploadRequest request, File file,
                FileUploadCallback callback) throws DracoonException;
        void cancelUploadFileAsync(String id) throws DracoonException;

        void downloadFile(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonException;
        void startDownloadFileAsync(String id, long nodeId, File file,
                FileDownloadCallback callback) throws DracoonException;
        void cancelDownloadFileAsync(String id) throws DracoonException;
    }

    public interface Shares {

    }

    public interface Events {

    }

    public abstract Server server();
    public abstract Account account();
    public abstract Users users();
    public abstract Groups groups();
    public abstract Roles roles();
    public abstract Permissions permissions();
    public abstract Nodes nodes();
    public abstract Shares shares();
    public abstract Events events();

    public static class Builder {

        private DracoonClientImpl mClient;

        public Builder(String serverUrl) {
            mClient = new DracoonClientImpl(serverUrl);
        }

        public Builder log(Log log) {
            mClient.setLog(log);
            return this;
        }

        public Builder accessToken(String accessToken) {
            mClient.setAccessToken(accessToken);
            return this;
        }

        public DracoonClient build() {
            mClient.init();
            return mClient;
        }

    }

}
