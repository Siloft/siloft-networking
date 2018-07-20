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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a TCP protocol. The TCP protocol contains the decoder
 * for all TCP protocol packets which are related to this protocol.
 *
 * @author Sander Veldhuis
 */
public abstract class TCPProtocol {

    /** List of all op codes added to this TCP protocol. */
    private final List<Short> opCodes;

    /** List of all TCP protocol packets added to this TCP protocol. */
    private final List<TCPProtocolPacket> packets;

    /**
     * Constructs a new TCP protocol which supports the specified TCP protocol
     * packets.
     *
     * @param supportedPackets
     *            the supported TCP protocol packets
     * @exception UnsupportedOperationException
     *                if two or more supported TCP protocol packets have the
     *                same op code
     */
    protected TCPProtocol(TCPProtocolPacket... supportedPackets) {
        opCodes = new ArrayList<Short>();
        packets = new ArrayList<TCPProtocolPacket>();

        for (TCPProtocolPacket supportedPacket : supportedPackets) {
            short opCode = ByteBuffer.wrap(supportedPacket.getData(), 0,
                    supportedPacket.getLength()).getShort();
            if (opCodes.contains(opCode)) {
                throw new UnsupportedOperationException("Duplicate op code");
            }

            opCodes.add(opCode);
            packets.add(supportedPacket);
        }
    }

    /**
     * Tries to decode the specified TCP packet to any of the added TCP protocol
     * packets of this TCP protocol.
     * <p>
     * The specified TCP packet could contain several TCP protocol packets.
     *
     * @param packet
     *            the TCP packet to decode
     *
     * @return the decoded TCP protocol packets
     */
    public TCPPacket[] decode(TCPPacket packet) {
        if (packet == null) {
            return new TCPPacket[0];
        }

        List<TCPProtocolPacket> decodedPackets =
                new ArrayList<TCPProtocolPacket>();
        byte[] data = packet.getData();
        int length = packet.getLength();
        int offset = 0;

        for (int i = 0; i < packets.size(); i++) {
            byte[] dataPart = Arrays.copyOfRange(data, offset, length);

            TCPProtocolPacket decodedPacket =
                    packets.get(i).decode(dataPart, length - offset);
            if (decodedPacket == null) {
                continue;
            }

            decodedPackets.add(decodedPacket);
            offset += decodedPacket.getLength();
            i = -1;
        }

        return decodedPackets.toArray(new TCPProtocolPacket[0]);
    }
}
