package com.dracoon.sdk.internal.service;

public class ThreadHelper {

    public boolean isThreadAlive(Thread thread) {
        return thread.isAlive();
    }

    public void interruptThread(Thread thread) {
        thread.interrupt();
    }

}
