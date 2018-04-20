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
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Verifies whether the <code>TCPServer</code> class is working properly.
 *
 * @author Sander Veldhuis
 */
public class TCPServerTest {

    /**
     * Test whether invalid name is not accepted.
     */
    @Test
    public void testInvalidName() {
        try {
            new TCPServer(null, 0, 0);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == IllegalArgumentException.class;
            assert e.getMessage() == "Invalid name";
        }
    }

    /**
     * Test whether invalid port is not accepted.
     */
    @Test
    public void testInvalidPort1() {
        try {
            new TCPServer("", -1, 0);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == IllegalArgumentException.class;
            assert e.getMessage() == "Invalid port";
        }
    }

    /**
     * Test whether invalid port is not accepted.
     */
    @Test
    public void testInvalidPort2() {
        try {
            new TCPServer("", 65536, 0);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == IllegalArgumentException.class;
            assert e.getMessage() == "Invalid port";
        }
    }

    /**
     * Test constructors and getters.
     */
    @Test
    public void testGetters() {
        TCPServer server1 = new TCPServer("Test");
        assert server1.getName() == "Test";
        assert server1.getPort() == 0;
        assert server1.getQueueLength() == 50;
        assert server1.getBindAddress() == null;
        assert server1.isConnected() == false;

        TCPServer server2 = new TCPServer("Test", 65535);
        assert server2.getName() == "Test";
        assert server2.getPort() == 65535;
        assert server2.getQueueLength() == 50;
        assert server2.getBindAddress() == null;
        assert server2.isConnected() == false;

        TCPServer server3 = new TCPServer("Test", 65535, 1);
        assert server3.getName() == "Test";
        assert server3.getPort() == 65535;
        assert server3.getQueueLength() == 1;
        assert server3.getBindAddress() == null;
        assert server3.isConnected() == false;

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            TCPServer server4 = new TCPServer("Test", 65535, 1, inetAddress);
            assert server4.getName() == "Test";
            assert server4.getPort() == 65535;
            assert server4.getQueueLength() == 1;
            assert server4.getBindAddress() == inetAddress;
            assert server4.isConnected() == false;
        } catch (UnknownHostException e) {
            assert false;
        }
    }

    /**
     * Test connecting and disconnecting the server.
     */
    @Test
    public void testConnectingDisconnecting() {
        new JFXPanel(); // JavaFX should be initialized

        TCPServer server = new TCPServer("Test");
        assert server.isConnected() == false;
        assert server.getPort() == 0;

        try {
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

        server.disconnect();
        assert server.isConnected() == false;
        assert server.getPort() == 0;
    }

    /**
     * Test accepting a client and client disconnects.
     */
    @Test
    public void testAcceptingClientDisconnects() {
        new JFXPanel(); // JavaFX should be initialized

        TCPServer server = new TCPServer("Test");
        try {
            server.connect();
            assert server.isConnected() == true;
        } catch (Exception e) {
            assert false;
        }

        try {
            Socket client = new Socket("localhost", server.getPort());
            assert client.isBound() == true;
            assert client.isClosed() == false;
            assert client.isConnected() == true;

            try {
                // Ensure client is accepted
                Thread.sleep(500);
            } catch (InterruptedException e) {
                assert false;
            }

            client.close();
            assert client.isBound() == true;
            assert client.isClosed() == true;
            assert client.isConnected() == true;
        } catch (Exception e) {
            assert false;
        }
    }

    /**
     * Test accepting a client and server disconnects.
     */
    @Test
    public void testAcceptingServerDisconnects() {
        new JFXPanel(); // JavaFX should be initialized

        TCPServer server = new TCPServer("Test");

        try {
            server.connect();
            assert server.isConnected() == true;
        } catch (Exception e) {
            assert false;
        }

        try {
            @SuppressWarnings("resource")
            Socket client = new Socket("localhost", server.getPort());
            assert client.isBound() == true;
            assert client.isClosed() == false;
            assert client.isConnected() == true;
        } catch (Exception e) {
            assert false;
        }

        try {
            // Ensure client is accepted
            Thread.sleep(500);
        } catch (InterruptedException e) {
            assert false;
        }

        server.disconnect();
    }

    /**
     * Test accepting a client and server disconnects client.
     */
    @Test
    public void testAcceptingClientDisconnected() {
        new JFXPanel(); // JavaFX should be initialized

        TCPServer server = new TCPServer("Test");

        try {
            server.connect();
            assert server.isConnected() == true;
        } catch (Exception e) {
            assert false;
        }

        try {
            @SuppressWarnings("resource")
            Socket client = new Socket("localhost", server.getPort());
            assert client.isBound() == true;
            assert client.isClosed() == false;
            assert client.isConnected() == true;

            try {
                // Ensure client is accepted
                Thread.sleep(500);
            } catch (InterruptedException e) {
                assert false;
            }

            server.disconnect(client.hashCode());
        } catch (Exception e) {
            assert false;
        }
    }
}
