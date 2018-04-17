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

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * This class represents an SSL client for transmitting and receiving TCP
 * packets encrypted by SSL or TLS.
 * <p>
 * An SSL client is the transmitting or receiving point for a packet delivery
 * service. Each packet transmitted or received on an SSL server is individually
 * addressed, routed, and encrypted. TCP provides reliable, ordered, and
 * error-checked delivery of a stream of octets (bytes) between applications
 * running on hosts communicating by an IP network. Packet delivery is
 * guaranteed.
 * <p>
 * The SSL client requires the JavaFX thread to be initialised. If your
 * application is not running as JavaFX application the JavaFX thread can simply
 * be initialised by calling: <code>new JFXPanel();</code>
 *
 * @author Sander Veldhuis
 */
public class SSLClient extends TCPClient {

    /** The key store. */
    private final String keyStore;

    /** The key store password. */
    private final String keyStorePass;

    /**
     * Constructs a new SSL client, that connects to the specified server port
     * on the local machine. The port must be between 0 and 65535, inclusive.
     *
     * @param name
     *            the client name
     * @param serverPort
     *            the server port number
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name, server port, or key store is invalid
     * @exception UnknownHostException
     *                if the local host name could not be resolved into an
     *                address
     */
    public SSLClient(String name, int serverPort, String keyStore,
            String keyStorePass) throws UnknownHostException {
        this(name, serverPort, InetAddress.getLocalHost(), keyStore,
                keyStorePass);
    }

    /**
     * Constructs a new SSL client, that connects to the specified server port
     * and server address. The port must be between 0 and 65535, inclusive.
     *
     * @param name
     *            the client name
     * @param serverPort
     *            the server port number
     * @param serverAddress
     *            the server InetAddress
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name, server port, server address, or key store is
     *                invalid
     */
    public SSLClient(String name, int serverPort, InetAddress serverAddress,
            String keyStore, String keyStorePass) {
        super(name, serverPort, serverAddress);
        if (keyStore == null) {
            throw new IllegalArgumentException("Invalid key store");
        }
        this.keyStore = keyStore;
        this.keyStorePass = keyStorePass;
    }

    /**
     * Tries to start this SSL client connection.
     *
     * @exception IOException
     *                if an I/O error occurs when opening the connection
     * @exception SecurityException
     *                if a security manager exists and its
     *                <code>checkListen</code> method doesn't allow the
     *                operation
     * @exception SocketException
     *                if the key store is invalid
     */
    @Override
    public synchronized void connect() throws IOException, SecurityException {
        if (isConnected()) {
            return;
        }

        System.setProperty("javax.net.ssl.trustStore", keyStore);
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);

        SSLSocketFactory sslFactory =
                (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = sslFactory.createSocket(getServerAddress(), getServerPort());
        ((SSLSocket) socket).startHandshake();
        createReceiveService();
        createTransmitService();
    }

    /**
     * Returns the key store of this SSL client.
     *
     * @return the key store
     */
    public synchronized String getKeyStore() {
        return keyStore;
    }

    /**
     * Returns the key store password of this SSL client.
     *
     * @return the key store password
     */
    public synchronized String getKeyStorePass() {
        return keyStorePass;
    }
}
