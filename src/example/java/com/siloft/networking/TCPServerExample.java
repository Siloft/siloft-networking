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

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class containing an example implementation of a TCP server based on a JavaFX
 * application. This server bounces the received TCP packets back to the client
 * it received it from.
 *
 * @author Sander Veldhuis
 */
public class TCPServerExample extends Application
        implements
            ConnectedListener,
            DisconnectedListener,
            ServerPacketListener {

    /** The TCP server. */
    private TCPServer server;

    /**
     * Entry method to start this application.
     *
     * @param args
     *            arguments for this application
     */
    public static void main(String[] args) {
        Application.launch(TCPServerExample.class, args);
    }

    /**
     * The main entry point for this application. The start method is called
     * after the system is ready for the application to begin running.
     *
     * @param stage
     *            the primary stage for this application
     *
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws Exception {
        server = new TCPServer("Test", 12345);

        server.addConnectedListener(this);
        server.addDisconnectedListener(this);
        server.addPacketListener(this);

        try {
            server.connect();
            System.out.println("TCP server '" + server.getName()
                    + "' started on port: " + server.getPort());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked after receiving a new TCP packet. Please note that the data of a
     * TCP packet could contain several packets.
     *
     * @param name
     *            the server name
     * @param id
     *            the client identifier
     * @param packet
     *            the TCP packet
     */
    @Override
    public void received(String name, int id, TCPPacket packet) {
        System.out.println("TCP server '" + name + "' received TCP packet of "
                + packet.getLength() + " bytes from " + id + ": "
                + new String(packet.getData()));
        server.transmit(id, packet);
    }

    /**
     * Invoked after a new client is connected.
     *
     * @param name
     *            the server name
     * @param id
     *            the client identifier
     */
    @Override
    public void connected(String name, int id) {
        System.out.println("TCP server '" + name + "' got new client: " + id);
    }

    /**
     * Invoked after a client is disconnected.
     *
     * @param name
     *            the server name
     * @param id
     *            the client identifier
     */
    @Override
    public void disconnected(String name, int id) {
        System.out.println("TCP server '" + name + "' lost client: " + id);
    }
}
