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
 * Class containing an example implementation of a TCP client based on a JavaFX
 * application. This client transmits a TCP packet after connection is
 * established.
 *
 * @author Sander Veldhuis
 */
public class TCPClientExample extends Application
        implements
            ClientPacketListener {

    /** The TCP client. */
    private TCPClient client;

    /**
     * Entry method to start this application.
     *
     * @param args
     *            arguments for this application
     */
    public static void main(String[] args) {
        Application.launch(TCPClientExample.class, args);
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
        client = new TCPClient("Test", 12345);

        client.addPacketListener(this);

        try {
            client.connect();
            System.out.println("TCP client '" + client.getName()
                    + "' started on port: " + client.getPort());

            byte[] data = "Hello, World!".getBytes();
            client.transmit(new TCPPacket(data, data.length));
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
     *            the client name
     * @param packet
     *            the TCP packet
     */
    @Override
    public void received(String name, TCPPacket packet) {
        System.out.println("TCP client '" + name + "' received TCP packet of "
                + packet.getLength() + " bytes: "
                + new String(packet.getData()));
    }
}
