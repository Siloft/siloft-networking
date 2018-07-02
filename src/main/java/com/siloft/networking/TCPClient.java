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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This class represents a TCP client for transmitting and receiving TCP
 * packets.
 * <p>
 * A TCP client is the transmitting or receiving point for a packet delivery
 * service. Each packet transmitted or received on a TCP server is individually
 * addressed and routed. TCP provides reliable, ordered, and error-checked
 * delivery of a stream of octets (bytes) between applications running on hosts
 * communicating by an IP network. Packet delivery is guaranteed.
 * <p>
 * The TCP client requires the JavaFX thread to be initialised. If your
 * application is not running as JavaFX application the JavaFX thread can simply
 * be initialised by calling: <code>new JFXPanel();</code>
 *
 * @author Sander Veldhuis
 */
public class TCPClient {

    /** The client socket holding the connection. */
    protected Socket socket;

    /** The client name. */
    private final String name;

    /** The server port number. */
    private final int serverPort;

    /** The server address. */
    private final InetAddress serverAddress;

    /** The protocol used for decoding packets. */
    private TCPProtocol protocol;

    /** The service for receiving data from the server. */
    private ReceiveService receiveService;

    /** The service for transmitting data to the server. */
    private TransmitService transmitService;

    /** List containing all listeners triggered upon newly received packets. */
    private final List<ClientPacketListener> packetListeners =
            new ArrayList<ClientPacketListener>();

    /**
     * Constructs a new TCP client, that connects to the specified server port
     * on the local machine. The port must be between 0 and 65535, inclusive.
     *
     * @param name
     *            the client name
     * @param serverPort
     *            the server port number
     *
     * @exception IllegalArgumentException
     *                if the name or server port is invalid
     * @exception UnknownHostException
     *                if the local host name could not be resolved into an
     *                address
     */
    public TCPClient(String name, int serverPort) throws UnknownHostException {
        this(name, serverPort, InetAddress.getLocalHost());
    }

    /**
     * Constructs a new TCP client, that connects to the specified server port
     * and server address. The port must be between 0 and 65535, inclusive.
     *
     * @param name
     *            the client name
     * @param serverPort
     *            the server port number
     * @param serverAddress
     *            the server InetAddress
     *
     * @exception IllegalArgumentException
     *                if the name, server port, or server address is invalid
     */
    public TCPClient(String name, int serverPort, InetAddress serverAddress) {
        if (name == null) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (serverPort < 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Invalid server port");
        }
        if (serverAddress == null) {
            throw new IllegalArgumentException("Invalid server address");
        }
        this.name = name;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    /**
     * Tries to start this TCP client connection.
     *
     * @exception IOException
     *                if an I/O error occurs when opening the connection
     * @exception SecurityException
     *                if a security manager exists and its
     *                <code>checkListen</code> method doesn't allow the
     *                operation
     */
    public synchronized void connect() throws IOException, SecurityException {
        if (isConnected()) {
            return;
        }
        socket = new Socket(serverAddress, serverPort);
        createReceiveService();
        createTransmitService();
    }

    /**
     * Tries to stop this TCP client connection.
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            // Ignore
        }
        socket = null;
        receiveService = null;
        transmitService = null;
    }

    /**
     * Schedules a TCP packet for transmission over this TCP client connection.
     *
     * @param packet
     *            the TCP packet
     */
    public void transmit(TCPPacket packet) {
        if (!isConnected() || packet == null) {
            return;
        }
        transmitService.enqueue(packet);
        transmitService.restart();
    }

    /**
     * Add a packet listener to this TCP client. The listener will be triggered
     * upon newly received packets.
     *
     * @param listener
     *            the listener
     */
    public synchronized void addPacketListener(ClientPacketListener listener) {
        packetListeners.add(listener);
    }

    /**
     * Remove a packet listener from this TCP client.
     *
     * @param listener
     *            the listener
     */
    public synchronized void removePacketListener(
            ClientPacketListener listener) {
        packetListeners.remove(listener);
    }

    /**
     * Returns the name of this TCP client.
     *
     * @return the name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Returns the port number of this TCP client.
     *
     * @return the port number, or <code>0</code> if not connected
     */
    public synchronized int getPort() {
        return (socket != null ? socket.getLocalPort() : 0);
    }

    /**
     * Returns the address of this TCP client.
     *
     * @return the address, or <code>null</code> if not connected
     */
    public InetAddress getAddress() {
        return (socket != null ? socket.getLocalAddress() : null);
    }

    /**
     * Returns the server port number of this TCP client.
     *
     * @return the server port number
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Returns the server address of this TCP client.
     *
     * @return the server address
     */
    public InetAddress getServerAddress() {
        return serverAddress;
    }

    /**
     * Returns the protocol of this TCP client.
     *
     * @return the protocol
     */
    public TCPProtocol getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of this TCP client.
     *
     * @param protocol
     *            the protocol
     */
    public void setProtocol(TCPProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Indicates whether this TCP client is connected.
     *
     * @return <code>true</code> if connected, or <code>false</code> otherwise
     */
    public synchronized boolean isConnected() {
        return (socket != null && socket.isBound() && socket.isConnected()
                && !socket.isClosed());
    }

    /**
     * Create a new receive service for this TCP client.
     */
    protected void createReceiveService() {
        receiveService = new ReceiveService(socket);
        receiveService.setExecutor(createExecutorService());
        receiveService.setOnFailed((value) -> {
            receiveFailed();
        });
        receiveService.setOnSucceeded((value) -> {
            receiveSucceeded();
        });
        receiveService.start();
    }

    /**
     * Create a new transmit service for this TCP client.
     */
    protected void createTransmitService() {
        transmitService = new TransmitService(socket);
        transmitService.setExecutor(createExecutorService());
        transmitService.setOnFailed((value) -> {
            transmitFailed();
        });
        transmitService.setOnSucceeded((value) -> {
            transmitSucceeded();
        });
        transmitService.start();
    }

    /**
     * Create a new executor service which is running as a daemon to ensure the
     * thread will not block the closure of the application.
     *
     * @return the executor service
     */
    private ExecutorService createExecutorService() {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                final Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        };
        return Executors.newSingleThreadExecutor(threadFactory);
    }

    /**
     * Invoked upon successfully finishing a receive task. Notifies all
     * listeners with the newly received TCP packet.
     */
    private void receiveSucceeded() {
        if (!isConnected()) {
            return;
        }
        TCPPacket receivedPacket = receiveService.getValue();

        // End-of-stream means disconnected
        if (receivedPacket.getLength() == -1) {
            disconnect();
            return;
        }

        TCPPacket[] packets = new TCPPacket[] { receivedPacket };
        if (protocol != null) {
            packets = protocol.decode(receivedPacket);
        }
        for (TCPPacket tcpPacket : packets) {
            for (ClientPacketListener listener : packetListeners) {
                listener.received(name, tcpPacket);
            }
        }

        if (receiveService != null) {
            receiveService.restart();
        }
    }

    /**
     * Invoked upon successfully finishing a transmit task.
     */
    private void transmitSucceeded() {
        // Nothing to do
    }

    /**
     * Invoked upon failed finishing a receive task. Disconnects this TCP
     * client.
     */
    private void receiveFailed() {
        disconnect();
    }

    /**
     * Invoked upon failed finishing a transmit task. Disconnects this TCP
     * client.
     */
    private void transmitFailed() {
        disconnect();
    }
}
