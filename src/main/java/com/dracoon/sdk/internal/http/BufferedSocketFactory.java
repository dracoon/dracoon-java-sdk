package com.dracoon.sdk.internal.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class BufferedSocketFactory extends SocketFactory {

    private final int mSendBufferSize;

    public BufferedSocketFactory(int sendBufferSize) {
        mSendBufferSize = sendBufferSize;
    }

    @Override
    public Socket createSocket() throws IOException {
        return updateSocket(new Socket());
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return updateSocket(new Socket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddr, int localPort)
            throws IOException {
        return updateSocket(new Socket(host, port, localAddr, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return updateSocket(new Socket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort)
            throws IOException {
        return updateSocket(new Socket(address, port, localAddr, localPort));
    }

    private Socket updateSocket(Socket socket) throws IOException {
        socket.setSendBufferSize(mSendBufferSize);
        return socket;
    }

}
