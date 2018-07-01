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

import javax.net.ssl.SSLServerSocketFactory;

/**
 * This class represents an SSL server for transmitting and receiving TCP
 * packets encrypted by SSL or TLS.
 * <p>
 * An SSL server is the transmitting or receiving point for a packet delivery
 * service. Each packet transmitted or received on an SSL server is individually
 * addressed, routed, and encrypted. TCP provides reliable, ordered, and
 * error-checked delivery of a stream of octets (bytes) between applications
 * running on hosts communicating by an IP network. Packet delivery is
 * guaranteed.
 * <p>
 * The SSL server requires the JavaFX thread to be initialised. If your
 * application is not running as JavaFX application the JavaFX thread can simply
 * be initialised by calling: <code>new JFXPanel();</code>
 *
 * @author Sander Veldhuis
 */
public class SSLServer extends TCPServer {

    /** The key store. */
    private final String keyStore;

    /** The key store password. */
    private final String keyStorePass;

    /**
     * Constructs a new SSL server, on any free port. The maximum queue length
     * for incoming connections is set to 50. If the queue is full the
     * connection request is ignored.
     * <p>
     * The server will accepting connections on any/all local addresses.
     * <p>
     * The maximum queue length for incoming connection indications (a request
     * to connect) is set to <code>50</code>. If a connection indication arrives
     * when the queue is full, the connection is refused.
     *
     * @param name
     *            the server name
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name or key store is invalid
     */
    public SSLServer(String name, String keyStore, String keyStorePass) {
        this(name, 0, 0, null, keyStore, keyStorePass);
    }

    /**
     * Constructs a new SSL server, bound to the specified port. A port of
     * <code>0</code> constructs an SSL server on any free port. The port must
     * be between 0 and 65535, inclusive.
     * <p>
     * The server will accepting connections on any/all local addresses.
     * <p>
     * The maximum queue length for incoming connection indications (a request
     * to connect) is set to <code>50</code>. If a connection indication arrives
     * when the queue is full, the connection is refused.
     *
     * @param name
     *            the server name
     * @param port
     *            the server port number
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name, port, or key store is invalid
     */
    public SSLServer(String name, int port, String keyStore,
            String keyStorePass) {
        this(name, port, 0, null, keyStore, keyStorePass);
    }

    /**
     * Constructs a new SSL server, bound to the specified port. A port of
     * <code>0</code> constructs an SSL server on any free port. The port must
     * be between 0 and 65535, inclusive.
     * <p>
     * The server will accepting connections on any/all local addresses.
     * <p>
     * The maximum queue length for incoming connection indications (a request
     * to connect) is set to the <code>queueLength</code> parameter. If a
     * connection indication arrives when the queue is full, the connection is
     * refused. The <code>queueLength</code> argument must be a positive value
     * greater than 0. If the value passed is equal or less than 0, then the
     * default value of <code>50</code> will be assumed.
     *
     * @param name
     *            the server name
     * @param port
     *            the server port number
     * @param queueLength
     *            the maximum length of the queue
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name, port, or key store is invalid
     */
    public SSLServer(String name, int port, int queueLength, String keyStore,
            String keyStorePass) {
        this(name, port, queueLength, null, keyStore, keyStorePass);
    }

    /**
     * Constructs a new SSL server, bound to the specified port. A port of
     * <code>0</code> constructs an SSL server on any free port. The port must
     * be between 0 and 65535, inclusive.
     * <p>
     * The bind address argument can be used on a multi-homed host for a TCP
     * server that will only accept connect requests to one of its addresses. If
     * <code>bindAddres</code> is null, it will default accepting connections on
     * any/all local addresses.
     * <p>
     * The maximum queue length for incoming connection indications (a request
     * to connect) is set to the <code>queueLength</code> parameter. If a
     * connection indication arrives when the queue is full, the connection is
     * refused. The <code>queueLength</code> argument must be a positive value
     * greater than 0. If the value passed is equal or less than 0, then the
     * default value of <code>50</code> will be assumed.
     *
     * @param name
     *            the server name
     * @param port
     *            the server port number
     * @param queueLength
     *            the maximum length of the queue
     * @param bindAddress
     *            the local InetAddress the server will bind to
     * @param keyStore
     *            the key store
     * @param keyStorePass
     *            the key store password
     *
     * @exception IllegalArgumentException
     *                if the name, port, or key store is invalid
     */
    public SSLServer(String name, int port, int queueLength,
            InetAddress bindAddress, String keyStore, String keyStorePass) {
        super(name, port, queueLength, bindAddress);
        if (keyStore == null) {
            throw new IllegalArgumentException("Invalid key store");
        }
        this.keyStore = keyStore;
        this.keyStorePass = keyStorePass;
    }

    /**
     * Tries to start this SSL server connection.
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

        System.setProperty("javax.net.ssl.keyStore", keyStore);
        if (keyStorePass != null) {
            System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
        }

        SSLServerSocketFactory sslFactory =
                (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        serverSocket = sslFactory.createServerSocket(getPort(),
                getQueueLength(), getBindAddress());
        createAcceptService();
    }

    /**
     * Returns the key store of this SSL server.
     *
     * @return the key store
     */
    public synchronized String getKeyStore() {
        return keyStore;
    }

    /**
     * Returns the key store password of this SSL server.
     *
     * @return the key store password
     */
    public synchronized String getKeyStorePass() {
        return keyStorePass;
    }
}
