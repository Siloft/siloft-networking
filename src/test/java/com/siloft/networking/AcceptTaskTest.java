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

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Verifies whether the <code>AcceptTask</code> class is working properly.
 *
 * @author Sander Veldhuis
 */
public class AcceptTaskTest {

    /**
     * Test whether <code>null</code> is not accepted.
     */
    @Test
    public void testNullPointerException() {
        try {
            new AcceptTask(null);
            assert false;
        } catch (Exception e) {
            assert e.getClass() == NullPointerException.class;
            assert e.getMessage() == "Socket is null";
        }
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        try {
            new AcceptTask(new ServerSocket());
        } catch (IOException e) {
            assert false;
        }
    }
}
