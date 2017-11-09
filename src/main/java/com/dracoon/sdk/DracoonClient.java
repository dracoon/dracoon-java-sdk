package com.dracoon.sdk;

import com.dracoon.sdk.error.DracoonException;
import com.dracoon.sdk.internal.DracoonClientImpl;
import com.dracoon.sdk.model.UploadCallback;
import com.dracoon.sdk.model.UploadRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;

import java.io.File;
import java.util.Date;

public abstract class DracoonClient {

    public interface Server {
        String getVersion() throws DracoonException;
        Date getTime() throws DracoonException;
    }

    public interface Account {

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

        Node upload(String id, UploadRequest request, File file, UploadCallback callback)
                throws DracoonException;
        void startUploadAsync(String id, UploadRequest request, File file, UploadCallback callback)
                throws DracoonException;
        void cancelUploadAsync(String id) throws DracoonException;
    }

    public interface Shares {

    }

    public interface Events {

    }

    protected Log mLog;

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log;
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
