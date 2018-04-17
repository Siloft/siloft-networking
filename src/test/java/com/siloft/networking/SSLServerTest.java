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

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Ignore;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;

/**
 * Verifies whether the <code>SSLServer</code> class is working properly.
 * 
 * @author Sander Veldhuis
 */
public class SSLServerTest {

    /**
     * Test whether invalid key store is not accepted.
     */
    @Test
    public void testInvalidName() {
        try {
            new SSLServer("", null, null);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == IllegalArgumentException.class;
            assert e.getMessage() == "Invalid key store";
        }
    }

    /**
     * Test constructors and getters.
     */
    @Test
    public void testGetters() {
        SSLServer server1 = new SSLServer("Test1", "Test2", "Test3");
        assert server1.getName() == "Test1";
        assert server1.getPort() == 0;
        assert server1.getQueueLength() == 50;
        assert server1.getBindAddress() == null;
        assert server1.getKeyStore() == "Test2";
        assert server1.getKeyStorePass() == "Test3";
        assert server1.isConnected() == false;

        SSLServer server2 = new SSLServer("Test", 65535, "Test2", "Test3");
        assert server2.getName() == "Test";
        assert server2.getPort() == 65535;
        assert server2.getQueueLength() == 50;
        assert server2.getBindAddress() == null;
        assert server2.getKeyStore() == "Test2";
        assert server2.getKeyStorePass() == "Test3";
        assert server2.isConnected() == false;

        SSLServer server3 = new SSLServer("Test", 65535, 1, "Test2", "Test3");
        assert server3.getName() == "Test";
        assert server3.getPort() == 65535;
        assert server3.getQueueLength() == 1;
        assert server3.getBindAddress() == null;
        assert server3.getKeyStore() == "Test2";
        assert server3.getKeyStorePass() == "Test3";
        assert server3.isConnected() == false;

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            SSLServer server4 = new SSLServer("Test", 65535, 1, inetAddress,
                    "Test2", "Test3");
            assert server4.getName() == "Test";
            assert server4.getPort() == 65535;
            assert server4.getQueueLength() == 1;
            assert server4.getBindAddress() == inetAddress;
            assert server4.getKeyStore() == "Test2";
            assert server4.getKeyStorePass() == "Test3";
            assert server4.isConnected() == false;
        } catch (UnknownHostException e) {
            assert false;
        }
    }

    /**
     * Test connecting with invalid key store.
     */
    @Test
    @Ignore("If key store is loaded before a new one does not work")
    public void testConnectingInvalidKeyStore() {
        try {
            SSLServer server = new SSLServer("Test1", "Test2", "Test3");
            server.connect();
            assert false;
        } catch (Exception e) {
            assert e.getClass() == SocketException.class;
        }
    }

    /**
     * Test connecting and disconnecting the server.
     */
    @Test
    public void testConnectingDisconnecting() {
        new JFXPanel(); // JavaFX should be initialized

        SSLServer server = new SSLServer("Test1",
                "src/main/test/com/siloft/networking/server.jks", "123456");

        try {
            assert server.isConnected() == false;
            assert server.getPort() == 0;

            server.connect();
            assert server.isConnected() == true;
            assert server.getPort() != 0;

            server.connect();
            assert server.isConnected() == true;
            assert server.getPort() != 0;
        } catch (Exception e) {
            assert false;
        }

        server.disconnect();
        assert server.isConnected() == false;
        assert server.getPort() == 0;
    }
}
