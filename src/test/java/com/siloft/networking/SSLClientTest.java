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

import javafx.embed.swing.JFXPanel;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Verifies whether the <code>SSLClient</code> class is working properly.
 *
 * @author Sander Veldhuis
 */
public class SSLClientTest {

    /**
     * Test whether invalid key store is not accepted.
     */
    @Test
    public void testInvalidName() {
        try {
            new SSLClient("", 0, null, null);
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
        try {
            SSLClient client1 = new SSLClient("Test1", 1, "Test2", "Test3");
            assert client1.getName() == "Test1";
            assert client1.getPort() == 0;
            assert client1.getAddress() == null;
            assert client1.getServerPort() == 1;
            assert client1.getServerAddress() == InetAddress.getLocalHost();
            assert client1.getKeyStore() == "Test2";
            assert client1.getKeyStorePass() == "Test3";
            assert client1.isConnected() == false;

            InetAddress inetAddress = InetAddress.getLocalHost();
            SSLClient client2 =
                    new SSLClient("Test", 65535, inetAddress, "Test2", "Test3");
            assert client2.getName() == "Test";
            assert client2.getPort() == 0;
            assert client2.getAddress() == null;
            assert client2.getServerPort() == 65535;
            assert client2.getServerAddress() == inetAddress;
            assert client1.getKeyStore() == "Test2";
            assert client1.getKeyStorePass() == "Test3";
            assert client1.isConnected() == false;
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
            SSLClient client = new SSLClient("Test1", 0, "Test2", "Test3");
            client.connect();
            assert false;
        } catch (Exception e) {
            assert e.getClass() == SocketException.class;
        }
    }

    /**
     * Test connecting and disconnecting the client.
     */
    @Test
    public void testConnectingDisconnecting() {
        new JFXPanel(); // JavaFX should be initialized

        SSLServer server = new SSLServer("Test",
                "src/test/resources/com/siloft/networking/SSLServerTest-Server.jks",
                "123456");
        try {
            server.connect();
        } catch (Exception e) {
            assert false;
        }

        try {
            SSLClient client = new SSLClient("Test1", server.getPort(),
                    "src/test/resources/com/siloft/networking/SSLServerTest-Client.jks",
                    "123456");

            assert client.isConnected() == false;
            assert client.getPort() == 0;

            client.connect();
            assert client.isConnected() == true;
            assert client.getPort() != 0;

            client.connect();
            assert client.isConnected() == true;
            assert client.getPort() != 0;

            client.disconnect();
            assert client.isConnected() == false;
            assert client.getPort() == 0;
        } catch (Exception e) {
            assert false;
        }

        server.disconnect();
    }
}
