/*
 * Copyright (c) 2018 Siloft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.siloft.networking;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A cancellable asynchronous computation which will wait till a new client is
 * connecting and extracts the dedicated socket.
 *
 * @author Sander Veldhuis
 */
final class AcceptTask extends Task<Socket> {

    /** The server socket holding the connection. */
    private final ServerSocket socket;

    /**
     * Constructs a new accept task for the specified server socket.
     *
     * @param socket
     *            the server socket
     *
     * @exception NullPointerException
     *                if the server socket is <code>null</code>
     */
    public AcceptTask(ServerSocket socket) {
        super();
        if (socket == null) {
            throw new NullPointerException("Socket is null");
        }
        this.socket = socket;
    }

    /**
     * Invoked after this accept task is started. Waits till a new client is
     * connecting to the server socket and extracts the socket.
     */
    @Override
    protected Socket call() throws IOException {
        return socket.accept();
    }
}
