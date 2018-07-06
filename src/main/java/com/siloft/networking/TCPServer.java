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

import javafx.concurrent.Worker.State;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This class represents a TCP server for transmitting and receiving TCP
 * packets.
 * <p>
 * A TCP server is the transmitting or receiving point for a packet delivery
 * service. Each packet transmitted or received on a TCP server is individually
 * addressed and routed. TCP provides reliable, ordered, and error-checked
 * delivery of a stream of octets (bytes) between applications running on hosts
 * communicating by an IP network. Packet delivery is guaranteed.
 * <p>
 * The TCP server requires the JavaFX thread to be initialised. If your
 * application is not running as JavaFX application the JavaFX thread can simply
 * be initialised by calling: <code>new JFXPanel();</code>
 *
 * @author Sander Veldhuis
 */
public class TCPServer {

    /** The server socket holding the connection. */
    protected ServerSocket serverSocket;

    /** The server name. */
    private final String name;

    /** The server port number. */
    private final int port;

    /** The maximum length of the queue. */
    private final int queueLength;

    /** The server bind address. */
    private final InetAddress bindAddress;

    /** List containing all accepted clients. */
    private final Map<Integer, Socket> clientSockets =
            new HashMap<Integer, Socket>();

    /** The protocol used for decoding packets. */
    private TCPProtocol protocol;

    /** The service for accepting new clients. */
    private AcceptService acceptService;

    /** The services for receiving data from clients. */
    private final Map<Integer, ReceiveService> receiveServices =
            new HashMap<Integer, ReceiveService>();

    /** The services for transmitting data to clients. */
    private final Map<Integer, TransmitService> transmitServices =
            new HashMap<Integer, TransmitService>();

    /** List containing all listeners triggered upon newly connected clients. */
    private final List<ServerConnectedListener> connectedListeners =
            new ArrayList<ServerConnectedListener>();

    /** List containing all listeners triggered upon disconnected clients. */
    private final List<ServerDisconnectedListener> disconnectedListeners =
            new ArrayList<ServerDisconnectedListener>();

    /** List containing all listeners triggered upon newly received packets. */
    private final List<ServerPacketListener> packetListeners =
            new ArrayList<ServerPacketListener>();

    /**
     * Constructs a new TCP server, on any free port. The maximum queue length
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
     *
     * @exception IllegalArgumentException
     *                if the name is invalid
     */
    public TCPServer(String name) {
        this(name, 0, 0, null);
    }

    /**
     * Constructs a new TCP server, bound to the specified port. A port of
     * <code>0</code> constructs a TCP server on any free port. The port must be
     * between 0 and 65535, inclusive.
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
     *
     * @exception IllegalArgumentException
     *                if the name or port is invalid
     */
    public TCPServer(String name, int port) {
        this(name, port, 0, null);
    }

    /**
     * Constructs a new TCP server, bound to the specified port. A port of
     * <code>0</code> constructs a TCP server on any free port. The port must be
     * between 0 and 65535, inclusive.
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
     *
     * @exception IllegalArgumentException
     *                if the name or port is invalid
     */
    public TCPServer(String name, int port, int queueLength) {
        this(name, port, queueLength, null);
    }

    /**
     * Constructs a new TCP server, bound to the specified port. A port of
     * <code>0</code> constructs a TCP server on any free port. The port must be
     * between 0 and 65535, inclusive.
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
     *
     * @exception IllegalArgumentException
     *                if the name or port is invalid
     */
    public TCPServer(String name, int port, int queueLength,
            InetAddress bindAddress) {
        if (name == null) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }
        if (queueLength < 1) {
            queueLength = 50;
        }
        this.name = name;
        this.port = port;
        this.queueLength = queueLength;
        this.bindAddress = bindAddress;
    }

    /**
     * Tries to start this TCP server connection.
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
        serverSocket = new ServerSocket(port, queueLength, bindAddress);
        createAcceptService();
    }

    /**
     * Tries to stop this TCP server connection.
     */
    public void disconnect() {
        clientSockets.forEach((id, clientSocket) -> {
            try {
                clientSocket.close();
            } catch (Exception e) {
                // Ignore
            }
        });
        try {
            serverSocket.close();
        } catch (Exception e) {
            // Ignore
        }
        serverSocket = null;
        clientSockets.clear();
        acceptService = null;
        receiveServices.clear();
        transmitServices.clear();
    }

    /**
     * Disconnects the specified client from this TCP server.
     *
     * @param id
     *            the client identifier
     */
    public void disconnect(int id) {
        if (!clientSockets.containsKey(id)) {
            return;
        }
        for (ServerDisconnectedListener listener : disconnectedListeners) {
            listener.disconnected(name, id);
        }

        try {
            clientSockets.remove(id).close();
        } catch (Exception ex) {
            // Ignore
        }
        receiveServices.remove(id);
        transmitServices.remove(id);
    }

    /**
     * Schedules a TCP packet for transmission to the specified client.
     *
     * @param id
     *            the client identifier
     * @param packet
     *            the TCP packet
     */
    public void transmit(int id, TCPPacket packet) {
        if (!isConnected() || packet == null) {
            return;
        }

        TransmitService service = transmitServices.get(id);
        if (service != null) {
            service.enqueue(packet);
            service.restart();
        }
    }

    /**
     * Add a connected listener to this TCP server. The listener will be
     * triggered upon newly connected clients.
     *
     * @param listener
     *            the listener
     */
    public synchronized void addConnectedListener(
            ServerConnectedListener listener) {
        connectedListeners.add(listener);
    }

    /**
     * Remove a connected listener from this TCP server.
     *
     * @param listener
     *            the listener
     */
    public synchronized void removeConnectedListener(
            ServerConnectedListener listener) {
        connectedListeners.remove(listener);
    }

    /**
     * Add a disconnected listener to this TCP server. The listener will be
     * triggered upon disconnected clients.
     *
     * @param listener
     *            the listener
     */
    public synchronized void addDisconnectedListener(
            ServerDisconnectedListener listener) {
        disconnectedListeners.add(listener);
    }

    /**
     * Remove a disconnected listener from this TCP server.
     *
     * @param listener
     *            the listener
     */
    public synchronized void removeDisconnectedListener(
            ServerDisconnectedListener listener) {
        disconnectedListeners.remove(listener);
    }

    /**
     * Add a packet listener to this TCP server. The listener will be triggered
     * upon newly received packets.
     *
     * @param listener
     *            the listener
     */
    public synchronized void addPacketListener(ServerPacketListener listener) {
        packetListeners.add(listener);
    }

    /**
     * Remove a packet listener from this TCP server.
     *
     * @param listener
     *            the listener
     */
    public synchronized void removePacketListener(
            ServerPacketListener listener) {
        packetListeners.remove(listener);
    }

    /**
     * Returns the name of this TCP server.
     *
     * @return the name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Returns the port number of this TCP server.
     *
     * @return the port number
     */
    public synchronized int getPort() {
        return (serverSocket != null ? serverSocket.getLocalPort() : port);
    }

    /**
     * Returns the maximum queue length of this TCP server.
     *
     * @return the maximum queue length
     */
    public synchronized int getQueueLength() {
        return queueLength;
    }

    /**
     * Returns the local bind address of this TCP server.
     *
     * @return the local bind address, or <code>null</code>
     */
    public synchronized InetAddress getBindAddress() {
        return bindAddress;
    }

    /**
     * Returns the protocol of this TCP server.
     *
     * @return the protocol
     */
    public TCPProtocol getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of this TCP server.
     *
     * @param protocol
     *            the protocol
     */
    public void setProtocol(TCPProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Indicates whether this TCP server is connected.
     *
     * @return <code>true</code> if connected, or <code>false</code> otherwise
     */
    public synchronized boolean isConnected() {
        return (serverSocket != null && serverSocket.isBound()
                && !serverSocket.isClosed());
    }

    /**
     * Create a new accept service for this TCP server.
     */
    protected void createAcceptService() {
        acceptService = new AcceptService(serverSocket);
        acceptService.setExecutor(createExecutorService());
        acceptService.setOnFailed((value) -> {
            acceptFailed();
        });
        acceptService.setOnSucceeded((value) -> {
            acceptSucceeded();
        });
        acceptService.start();
    }

    /**
     * Create a new receive service for the specified socket.
     *
     * @param socket
     *            the socket
     */
    protected void createReceiveService(Socket socket) {
        ReceiveService receiveService = new ReceiveService(socket);
        receiveService.setExecutor(createExecutorService());
        receiveService.setOnFailed((value) -> {
            receiveFailed();
        });
        receiveService.setOnSucceeded((value) -> {
            receiveSucceeded();
        });
        receiveService.start();
        receiveServices.put(socket.hashCode(), receiveService);
    }

    /**
     * Create a new transmit service for the specified socket.
     *
     * @param socket
     *            the socket
     */
    protected void createTransmitService(Socket socket) {
        TransmitService transmitService = new TransmitService(socket);
        transmitService.setExecutor(createExecutorService());
        transmitService.setOnFailed((value) -> {
            transmitFailed();
        });
        transmitService.setOnSucceeded((value) -> {
            transmitSucceeded();
        });
        transmitService.start();
        transmitServices.put(socket.hashCode(), transmitService);
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
     * Invoked upon successfully finishing an accept task. Notifies all
     * listeners with the newly connected client.
     */
    private void acceptSucceeded() {
        if (!isConnected()) {
            return;
        }

        Socket socket = acceptService.getValue();
        acceptService.restart();

        clientSockets.put(socket.hashCode(), socket);
        createReceiveService(socket);
        createTransmitService(socket);

        for (ServerConnectedListener listener : connectedListeners) {
            listener.connected(name, socket.hashCode());
        }
    }

    /**
     * Invoked upon successfully finishing a receive task. Notifies all
     * listeners with the newly received TCP packet.
     */
    private void receiveSucceeded() {
        if (!isConnected()) {
            return;
        }

        final Map<Integer, ReceiveService> services = Collections
                .synchronizedMap(new HashMap<Integer, ReceiveService>());
        services.putAll(receiveServices);
        services.forEach((id, service) -> {
            if (service.getState() != State.SUCCEEDED) {
                return;
            }
            TCPPacket receivedPacket = service.getValue();

            // End-of-stream means disconnected
            if (receivedPacket.getLength() == -1) {
                disconnect(id);
                return;
            }

            TCPPacket[] packets = new TCPPacket[] { receivedPacket };
            if (protocol != null) {
                packets = protocol.decode(receivedPacket);
            }
            for (TCPPacket tcpPacket : packets) {
                for (ServerPacketListener listener : packetListeners) {
                    listener.received(name, id, tcpPacket);
                }
            }

            service.restart();
        });
    }

    /**
     * Invoked upon successfully finishing a transmit task.
     */
    private void transmitSucceeded() {
        // Nothing to do
    }

    /**
     * Invoked upon failed finishing an accept task. Restarts the accept task if
     * the TCP server is still connected.
     */
    private void acceptFailed() {
        if (isConnected()) {
            acceptService.restart();
        }
    }

    /**
     * Invoked upon failed finishing a receive task. Disconnects the failed
     * client socket.
     */
    private void receiveFailed() {
        if (!isConnected()) {
            return;
        }

        final Map<Integer, ReceiveService> services = Collections
                .synchronizedMap(new HashMap<Integer, ReceiveService>());
        services.putAll(receiveServices);
        services.forEach((id, service) -> {
            if (service.getState() == State.FAILED) {
                disconnect(id);
            }
        });
    }

    /**
     * Invoked upon failed finishing a transmit task. Disconnects the failed
     * client socket.
     */
    private void transmitFailed() {
        if (!isConnected()) {
            return;
        }

        final Map<Integer, TransmitService> services = Collections
                .synchronizedMap(new HashMap<Integer, TransmitService>());
        services.putAll(transmitServices);
        services.forEach((id, service) -> {
            if (service.getState() == State.FAILED) {
                disconnect(id);
            }
        });
    }
}
