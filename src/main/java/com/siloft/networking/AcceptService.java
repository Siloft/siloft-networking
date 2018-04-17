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

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * A background thread which handles connecting clients asynchronously from the
 * specified server socket.
 *
 * @author Sander Veldhuis
 */
final class AcceptService extends Service<Socket> {

    /** The server socket holding the connection. */
    private final ServerSocket socket;

    /**
     * Constructs a new accept service for the specified server socket.
     *
     * @param socket
     *            the server socket
     *
     * @exception NullPointerException
     *                if the server socket is <code>null</code>
     */
    public AcceptService(ServerSocket socket) {
        if (socket == null) {
            throw new NullPointerException("Socket is null");
        }
        this.socket = socket;
    }

    /**
     * Invoked after this accept service is started. Creates a new
     * <code>AcceptTask</code> to handle connecting clients.
     *
     * @return the created <code>AcceptTask</code>
     */
    @Override
    protected Task<Socket> createTask() {
        return new AcceptTask(socket);
    }
}
