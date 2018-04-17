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

/**
 * This class represents a TCP packet.
 * <p>
 * TCP provides reliable, ordered, and error-checked delivery of a stream of
 * octets (bytes) between applications running on hosts communicating by an IP
 * network. Packet delivery is guaranteed.
 *
 * @author Sander Veldhuis
 */
public class TCPPacket {

    /** The packet data. */
    private final byte[] data;

    /** The packet length. */
    private final int length;

    /**
     * Constructs a new TCP packet.
     * <p>
     * The <code>length</code> argument must be less than or equal to
     * <code>data.length</code>.
     *
     * @param data
     *            the packet data
     * @param length
     *            the packet length
     *
     * @exception NullPointerException
     *                if the data is <code>null</code>
     * @exception IllegalArgumentException
     *                if the length is invalid
     */
    public TCPPacket(byte[] data, int length) {
        if (length > data.length) {
            throw new IllegalArgumentException("Invalid length");
        }
        this.data = data;
        this.length = length;
    }

    /**
     * Returns the data buffer of this TCP packet.
     *
     * @return the data buffer
     */
    public synchronized byte[] getData() {
        return data;
    }

    /**
     * Returns the data length of this TCP packet.
     *
     * @return the data length
     */
    public synchronized int getLength() {
        return length;
    }
}
