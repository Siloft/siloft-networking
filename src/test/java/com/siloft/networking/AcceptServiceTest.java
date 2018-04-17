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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import javafx.concurrent.Task;

/**
 * Verifies whether the <code>AcceptService</code> class is working properly.
 * 
 * @author Sander Veldhuis
 */
public class AcceptServiceTest {

    /**
     * Test whether <code>null</code> is not accepted.
     */
    @Test(expected = NullPointerException.class)
    public void testNullPointerException() {
        new AcceptService(null);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        try {
            new AcceptService(new ServerSocket());
        } catch (IOException e) {
            assert false;
        }
    }

    /**
     * Test creating a task.
     */
    @Test
    public void testCreateTask() {
        try {
            AcceptService service = new AcceptService(new ServerSocket());
            Task<Socket> task = service.createTask();

            assert task != null;
        } catch (IOException e) {
            assert false;
        }
    }
}
