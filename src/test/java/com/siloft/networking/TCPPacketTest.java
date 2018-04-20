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

import org.junit.Test;

/**
 * Verifies whether the <code>TCPPacket</code> class is working properly.
 *
 * @author Sander Veldhuis
 */
public class TCPPacketTest {

    /**
     * Test whether <code>null</code> is not accepted.
     */
    @Test
    public void testNullPointerException() {
        try {
            new TCPPacket(null, 0);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == NullPointerException.class;
        }
    }

    /**
     * Test whether invalid length is not accepted.
     */
    @Test
    public void testIllegalArgumentException() {
        try {
            new TCPPacket(new byte[0], 1);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == IllegalArgumentException.class;
            assert e.getMessage() == "Invalid length";
        }
    }

    /**
     * Test constructing and getters.
     */
    @Test
    public void testGetters() {
        TCPPacket packet = new TCPPacket(new byte[] { 0, 1, 2, 3 }, 3);

        assert packet != null;
        assert packet.getLength() == 3;
        assert packet.getData().length == 4;
        assert packet.getData()[0] == 0;
        assert packet.getData()[1] == 1;
        assert packet.getData()[2] == 2;
        assert packet.getData()[3] == 3;
    }
}
