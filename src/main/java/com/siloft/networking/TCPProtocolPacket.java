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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * This class represents a TCP protocol packet. In addition to the standard
 * <code>TCPPacket</code> this class supports field definitions in the
 * <code>TCPPacket</code> which will be used during transmission and receiving.
 * <p>
 * TCP provides reliable, ordered, and error-checked delivery of a stream of
 * octets (bytes) between applications running on hosts communicating by an IP
 * network. Packet delivery is guaranteed.
 * <p>
 * The following types are allowed:
 * <ul>
 * <li><code>byte</code> - uses 8 bits during transmission</li>
 * <li><code>short</code> - uses 16 bits during transmission</li>
 * <li><code>int</code> - uses 32 bits during transmission</li>
 * <li><code>long</code> - uses 64 bits during transmission</li>
 * <li><code>float</code> - uses 32 bits during transmission</li>
 * <li><code>double</code> - uses 64 bits during transmission</li>
 * <li><code>boolean</code> - uses 8 bits during transmission</li>
 * <li><code>String</code> - uses 8*n+8 bits during transmission</li>
 * <li><code>byte[]</code> - uses 8*n+8 bits during transmission</li>
 * </ul>
 * <p>
 * To define fields in a TCP protocol packet use the following parameter
 * definition: <code>public [TYPE] [NAME];</code> The <code>opCode</code>
 * parameter is always required as first parameter in a TCP protocol packet:
 * <code>public final short opCode = [NUMBER];</code>
 *
 * @author Sander Veldhuis
 */
public class TCPProtocolPacket extends TCPPacket {

    /** The op code field name. */
    private static final String OP_CODE_NAME = "opCode";

    /** The name of the <code>byte</code> type. */
    private static final String BYTE_TYPE = "byte";

    /** The name of the <code>short</code> type. */
    private static final String SHORT_TYPE = "short";

    /** The name of the <code>int</code> type. */
    private static final String INT_TYPE = "int";

    /** The name of the <code>long</code> type. */
    private static final String LONG_TYPE = "long";

    /** The name of the <code>float</code> type. */
    private static final String FLOAT_TYPE = "float";

    /** The name of the <code>double</code> type. */
    private static final String DOUBLE_TYPE = "double";

    /** The name of the <code>boolean</code> type. */
    private static final String BOOLEAN_TYPE = "boolean";

    /** The name of the <code>String</code> type. */
    private static final String STRING_TYPE = "java.lang.String";

    /** The name of the <code>byte[]</code> type. */
    private static final String BYTE_ARRAY_TYPE = "byte[]";

    /** The length of the <code>byte</code> type. */
    private static final int BYTE_LENGTH = 1;

    /** The length of the <code>short</code> type. */
    private static final int SHORT_LENGTH = 2;

    /** The length of the <code>int</code> type. */
    private static final int INT_LENGTH = 4;

    /** The length of the <code>long</code> type. */
    private static final int LONG_LENGTH = 8;

    /** The length of the <code>float</code> type. */
    private static final int FLOAT_LENGTH = 4;

    /** The length of the <code>double</code> type. */
    private static final int DOUBLE_LENGTH = 8;

    /** The length of the <code>boolean</code> type. */
    private static final int BOOLEAN_LENGTH = 1;

    /** The length of the <code>String</code> type. */
    private static final int STRING_LENGTH = 1;

    /** The length of the <code>byte[]</code> type. */
    private static final int BYTE_ARRAY_LENGTH = 1;

    /**
     * Constructs a new TCP protocol packet.
     *
     * @exception IllegalArgumentException
     *                if any of the fields is not valid
     */
    protected TCPProtocolPacket() {
        super(new byte[0], 0);
        validateFields();
    }

    /**
     * Converts the specified data to a TCP protocol packet, or
     * <code>null</code> if the data does not contain a valid TCP protocol
     * packet.
     *
     * @param data
     *            the data
     * @param length
     *            the data length
     *
     * @return the TCP protocol packet upon correctly decoding the data, or
     *         <code>null</code> otherwise
     */
    public TCPProtocolPacket decode(byte data[], int length) {
        if (data == null || length < data.length) {
            return null;
        }

        TCPProtocolPacket tcpPacket;
        try {
            tcpPacket = this.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }

        int i = 0;
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }

            try {
                String typeName = field.getGenericType().getTypeName();

                if (typeName.equals(BYTE_TYPE)) {
                    if (length - i < BYTE_LENGTH) {
                        return null;
                    }
                    field.setByte(tcpPacket, data[i]);
                    i += BYTE_LENGTH;
                } else if (typeName.equals(SHORT_TYPE)) {
                    if (length - i < SHORT_LENGTH) {
                        return null;
                    }
                    short value =
                            ByteBuffer.wrap(data, i, length - i).getShort();
                    if (field.getName().equals(OP_CODE_NAME)) {
                        if (field.getShort(tcpPacket) != value) {
                            return null;
                        }
                    } else {
                        field.setShort(tcpPacket, value);
                    }
                    i += SHORT_LENGTH;
                } else if (typeName.equals(INT_TYPE)) {
                    if (length - i < INT_LENGTH) {
                        return null;
                    }
                    int value = ByteBuffer.wrap(data, i, INT_LENGTH).getInt();
                    field.setInt(tcpPacket, value);
                    i += INT_LENGTH;
                } else if (typeName.equals(LONG_TYPE)) {
                    if (length - i < LONG_LENGTH) {
                        return null;
                    }
                    long value = ByteBuffer.wrap(data, i, length - i).getLong();
                    field.setLong(tcpPacket, value);
                    i += LONG_LENGTH;
                } else if (typeName.equals(FLOAT_TYPE)) {
                    if (length - i < FLOAT_LENGTH) {
                        return null;
                    }
                    float value =
                            ByteBuffer.wrap(data, i, length - i).getFloat();
                    field.setFloat(tcpPacket, value);
                    i += FLOAT_LENGTH;
                } else if (typeName.equals(DOUBLE_TYPE)) {
                    if (length - i < DOUBLE_LENGTH) {
                        return null;
                    }
                    double value =
                            ByteBuffer.wrap(data, i, length - i).getDouble();
                    field.setDouble(tcpPacket, value);
                    i += DOUBLE_LENGTH;
                } else if (typeName.equals(BOOLEAN_TYPE)) {
                    if (length - i < BOOLEAN_LENGTH) {
                        return null;
                    }
                    field.setBoolean(tcpPacket, (data[i] == 1 ? true : false));
                    i += BOOLEAN_LENGTH;
                } else if (typeName.equals(STRING_TYPE)) {
                    if (length - i < STRING_LENGTH) {
                        return null;
                    }
                    byte len = data[i++];
                    if (length - i < len) {
                        return null;
                    }
                    String value =
                            new String(data, i, len, StandardCharsets.UTF_8);
                    field.set(tcpPacket, value);
                    i += len;
                } else if (typeName.equals(BYTE_ARRAY_TYPE)) {
                    if (length - i < BYTE_ARRAY_LENGTH) {
                        return null;
                    }
                    byte len = data[i++];
                    if (length - i < len) {
                        return null;
                    }
                    byte[] value = Arrays.copyOfRange(data, i, i + len);
                    field.set(tcpPacket, value);
                    i += len;
                }
            } catch (Exception e) {
                return null;
            }
        }

        return tcpPacket;
    }

    /**
     * Returns the data buffer of this TCP protocol packet.
     *
     * @return the data buffer
     */
    @Override
    public synchronized byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(getLength());

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }

            try {
                String typeName = field.getGenericType().getTypeName();
                byte[] bytes = new byte[0];

                if (typeName.equals(BYTE_TYPE)) {
                    byte value = field.getByte(this);
                    bytes = ByteBuffer.allocate(BYTE_LENGTH).put(value).array();
                } else if (typeName.equals(SHORT_TYPE)) {
                    short value = field.getShort(this);
                    bytes = ByteBuffer.allocate(SHORT_LENGTH).putShort(value)
                            .array();
                } else if (typeName.equals(INT_TYPE)) {
                    int value = field.getInt(this);
                    bytes = ByteBuffer.allocate(INT_LENGTH).putInt(value)
                            .array();
                } else if (typeName.equals(LONG_TYPE)) {
                    long value = field.getLong(this);
                    bytes = ByteBuffer.allocate(LONG_LENGTH).putLong(value)
                            .array();
                } else if (typeName.equals(FLOAT_TYPE)) {
                    float value = field.getFloat(this);
                    bytes = ByteBuffer.allocate(FLOAT_LENGTH).putFloat(value)
                            .array();
                } else if (typeName.equals(DOUBLE_TYPE)) {
                    double value = field.getDouble(this);
                    bytes = ByteBuffer.allocate(DOUBLE_LENGTH).putDouble(value)
                            .array();
                } else if (typeName.equals(BOOLEAN_TYPE)) {
                    boolean value = field.getBoolean(this);
                    bytes = ByteBuffer.allocate(BOOLEAN_LENGTH)
                            .put((value ? (byte) 1 : (byte) 0)).array();
                } else if (typeName.equals(STRING_TYPE)) {
                    String value = (String) field.get(this);
                    if (value == null) {
                        buffer.put((byte) 0);
                    } else {
                        bytes = value.getBytes(StandardCharsets.UTF_8);
                        buffer.put((byte) bytes.length);
                    }
                } else if (typeName.equals(BYTE_ARRAY_TYPE)) {
                    byte[] value = (byte[]) field.get(this);
                    if (value == null) {
                        buffer.put((byte) 0);
                    } else {
                        bytes = value;
                        buffer.put((byte) bytes.length);
                    }
                }

                buffer.put(bytes);
            } catch (Exception e) {
                // Ignore
            }
        }

        return buffer.array();
    }

    /**
     * Returns the data length of this TCP protocol packet.
     *
     * @return the data length
     */
    @Override
    public synchronized int getLength() {
        int length = 0;

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }

            try {
                String typeName = field.getGenericType().getTypeName();

                if (typeName.equals(BYTE_TYPE)) {
                    length += BYTE_LENGTH;
                } else if (typeName.equals(SHORT_TYPE)) {
                    length += SHORT_LENGTH;
                } else if (typeName.equals(INT_TYPE)) {
                    length += INT_LENGTH;
                } else if (typeName.equals(LONG_TYPE)) {
                    length += LONG_LENGTH;
                } else if (typeName.equals(FLOAT_TYPE)) {
                    length += FLOAT_LENGTH;
                } else if (typeName.equals(DOUBLE_TYPE)) {
                    length += DOUBLE_LENGTH;
                } else if (typeName.equals(BOOLEAN_TYPE)) {
                    length += BOOLEAN_LENGTH;
                } else if (typeName.equals(STRING_TYPE)) {
                    length += STRING_LENGTH;
                    length += ((String) field.get(this)).length();
                } else if (typeName.equals(BYTE_ARRAY_TYPE)) {
                    length += BYTE_ARRAY_LENGTH;
                    length += ((byte[]) field.get(this)).length;
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        return length;
    }

    /**
     * Validate all fields of this TCP protocol packet. The first field is
     * expected to be the <code>opCode</code> field.
     */
    private void validateFields() {
        boolean opCodeExpected = true;

        for (Field field : getClass().getDeclaredFields()) {
            boolean isSynthetic = field.isSynthetic();
            int modifiers = field.getModifiers();
            String typeName = field.getGenericType().getTypeName();

            if (isSynthetic) {
                continue;
            }

            if (!Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Field should be public");
            } else if (Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(
                        "Field should not be static");
            } else if (Modifier.isStrict(modifiers)) {
                throw new IllegalArgumentException(
                        "Field should not be strict");
            } else if (Modifier.isTransient(modifiers)) {
                throw new IllegalArgumentException(
                        "Field should not be transient");
            } else if (Modifier.isVolatile(modifiers)) {
                throw new IllegalArgumentException(
                        "Field should not be volatile");
            }

            if (opCodeExpected) {
                if (!Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException(
                            "Field opCode should be final");
                } else if (!typeName.equals(SHORT_TYPE)) {
                    throw new IllegalArgumentException(
                            "Field opCode should be short");
                } else if (!field.getName().equals(OP_CODE_NAME)) {
                    throw new IllegalArgumentException(
                            "Field opCode invalid name");
                }
                opCodeExpected = false;
            } else {
                if (Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException(
                            "Field should not be final");
                } else if (!typeName.equals(BYTE_TYPE)
                        && !typeName.equals(SHORT_TYPE)
                        && !typeName.equals(INT_TYPE)
                        && !typeName.equals(LONG_TYPE)
                        && !typeName.equals(FLOAT_TYPE)
                        && !typeName.equals(DOUBLE_TYPE)
                        && !typeName.equals(BOOLEAN_TYPE)
                        && !typeName.equals(STRING_TYPE)
                        && !typeName.equals(BYTE_ARRAY_TYPE)) {
                    throw new IllegalArgumentException("Field type not valid");
                }
            }
        }
    }
}
