package com.dracoon.sdk.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for uploading a file as a stream of bytes.
 */
public abstract class FileUploadStream extends OutputStream {

    /**
     * Completes an upload. A completed upload stream cannot perform write operations and cannot be
     * reopened.
     *
     * @return the new node, or <code>null</code> if the completion was interrupted
     *
     * @throws IOException if an I/O error occurs.
     */
    public abstract Node complete() throws IOException;

}
