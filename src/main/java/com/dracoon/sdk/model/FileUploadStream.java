package com.dracoon.sdk.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for uploading a file as a stream of bytes.
 */
public abstract class FileUploadStream extends OutputStream {

    /**
     * Marks the upload as completed. A completed upload stream cannot perform write operations and
     * cannot be reopened.
     *
     * @throws IOException if an I/O error occurs.
     */
    public abstract void complete() throws IOException;

}
