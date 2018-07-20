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

import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A background thread which handles transmitting data asynchronously over the
 * specified socket.
 *
 * @author Sander Veldhuis
 */
final class TransmitService extends Service<TCPPacket> {

    /** The socket holding the connection. */
    private final Socket socket;

    /** The queue holding the TCP packets to be transmitted. */
    private final List<TCPPacket> queue = new LinkedList<TCPPacket>();

    /**
     * Constructs a new transmit service for the specified socket.
     *
     * @param socket
     *            the socket
     *
     * @exception NullPointerException
     *                if the socket is <code>null</code>
     */
    public TransmitService(Socket socket) {
        if (socket == null) {
            throw new NullPointerException("Socket is null");
        }
        this.socket = socket;
    }

    /**
     * Enqueue a TCP packet that need to be transmitted over the socket.
     *
     * @param packet
     *            the TCP packet
     */
    public void enqueue(TCPPacket packet) {
        queue.add(packet);
    }

    /**
     * Invoked after this transmit service is started. Creates a new
     * <code>TransmitTask</code> to handle data to be transmitted.
     *
     * @return the created <code>TransmitTask</code>
     */
    @Override
    protected Task<TCPPacket> createTask() {
        final List<TCPPacket> packets =
                Collections.synchronizedList(new LinkedList<TCPPacket>());

        packets.addAll(queue);

        TransmitTask task = new TransmitTask(socket, packets);
        task.valueProperty().addListener((listener) -> {
            if (task.getValue() != null) {
                queue.remove(task.getValue());
            }
        });

        return task;
    }
}
