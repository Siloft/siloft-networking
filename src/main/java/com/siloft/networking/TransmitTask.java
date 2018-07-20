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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * A cancellable asynchronous computation which will transmit the listed TCP
 * packets over the dedicated socket.
 */
final class TransmitTask extends Task<TCPPacket> {

    /** The socket holding the connection. */
    private final Socket socket;

    /** The list holding the TCP packets to be transmitted. */
    private final List<TCPPacket> packets;

    /**
     * Constructs a new transmit task for the specified socket and to be
     * transmitted TCP packets.
     *
     * @param socket
     *            the socket
     * @param packets
     *            the TCP packet list
     *
     * @exception NullPointerException
     *                if the socket or packet list is <code>null</code>
     */
    public TransmitTask(Socket socket, List<TCPPacket> packets) {
        super();
        if (socket == null) {
            throw new NullPointerException("Socket is null");
        }
        if (packets == null) {
            throw new NullPointerException("Packet list is null");
        }
        this.socket = socket;
        this.packets = packets;
    }

    /**
     * Invoked after this transmit task is started. Transmits the TCP packets
     * over the socket. It returns all packets which failed to transmit due to a
     * cancellation of this task.
     */
    @Override
    protected TCPPacket call() throws IOException {
        while (!packets.isEmpty()) {
            final TCPPacket packet = packets.remove(0);

            DataOutputStream writer =
                    new DataOutputStream(socket.getOutputStream());
            writer.write(packet.getData(), 0, packet.getLength());
            // Do not close writer because causes close of socket

            updateValue(packet);
        }
        return null;
    }
}
