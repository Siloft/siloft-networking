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
 * Class containing an example implementation of an SSL client based on a JavaFX
 * application. This client transmits two TCP protocol packets (messages) after
 * connection is established.
 *
 * @author Sander Veldhuis
 */
public class SSLProtocolClientExample extends Application
        implements
            ClientPacketListener {

    /** The SSL client. */
    private SSLClient client;

    /**
     * Entry method to start this application.
     *
     * @param args
     *            arguments for this application
     */
    public static void main(String[] args) {
        Application.launch(SSLProtocolClientExample.class, args);
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
        client = new SSLClient("Test", 12345,
                "src/example/resources/com/siloft/networking/SSLClientExample.jks",
                "123456");

        client.addPacketListener(this);

        client.setProtocol(new ExampleProtocol());

        try {
            client.connect();
            System.out.println("SSL client '" + client.getName()
                    + "' started on port: " + client.getPort());

            Example1Msg packet1 = new Example1Msg();
            packet1.id = 12;
            packet1.name = "Siloft";
            packet1.age = 99;
            client.transmit(packet1);

            Example2Msg packet2 = new Example2Msg();
            packet2.street = "Wall Street";
            packet2.number = 123;
            client.transmit(packet2);
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
        System.out.println("SSL client '" + name + "' received TCP packet of "
                + packet.getLength() + " bytes");

        if (packet instanceof Example1Msg) {
            Example1Msg packet1 = (Example1Msg) packet;
            System.out.println("    id: " + packet1.id + ", name: "
                    + packet1.name + ", age: " + packet1.age);
        } else if (packet instanceof Example2Msg) {
            Example2Msg packet2 = (Example2Msg) packet;
            System.out.println("    street: " + packet2.street + ", number: "
                    + packet2.number);
        }
    }
}
