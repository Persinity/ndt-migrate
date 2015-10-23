/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.common;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.persinity.common.invariant.NotNull;

/**
 * Output stream that writes to Log4j logger.<BR>
 * Adapted from http://sysgears.com/articles/how-to-redirect-stdout-and-stderr-writing-to-a-log4j-appender/, which
 * adapted from Jim Moore
 *
 * @author Doichin Yordanov
 */
public class Log4jOutputStream extends OutputStream {

    /**
     * Default number of bytes in the buffer.
     */
    private static final int DEFAULT_BUFFER_LENGTH = 2048;

    /**
     * Indicates stream state.
     */
    private boolean hasBeenClosed = false;

    /**
     * Internal buffer where data is stored.
     */
    private byte[] buf;

    /**
     * The number of valid bytes in the buffer.
     */
    private int count;

    /**
     * The log level.
     */
    private final Level level;

    /**
     * The logger to write to.
     */
    private final Logger callerLogger;

    /**
     * Creates the Logging instance to flush to the given logger.<BR>
     * <b>Caution:</b>&nbsp;Line numbers are not correctly logged.
     *
     * @param caller
     *         the caller that will log through this stream.
     * @param level
     *         the log level
     * @throws IllegalArgumentException
     *         in case if one of arguments is null.
     */
    public Log4jOutputStream(final Logger callerLogger, Level level) throws IllegalArgumentException {
        new NotNull("callerLogger").enforce(callerLogger);
        this.callerLogger = callerLogger;
        if (level == null) {
            level = Level.INFO;
        }
        this.level = level;
        buf = new byte[DEFAULT_BUFFER_LENGTH];
        count = 0;
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b
     *         the byte to write
     * @throws IOException
     *         if an I/O error occurs.
     */
    @Override
    public void write(final int b) throws IOException {
        if (hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        }
        // don't log nulls
        if (b == 0) {
            return;
        }
        // would this be writing past the buffer?
        if (count >= buf.length) {
            buf = extendBuffer(buf, DEFAULT_BUFFER_LENGTH);
        }

        buf[count++] = (byte) b;
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be written out.
     */
    @Override
    public void flush() {
        if (count == 0) {
            return;
        }
        final String str = new String(buf, 0, count);
        if (!str.isEmpty()) {
            callerLogger.log(callerLogger.getName(), level, str, null);
        }
        count = 0;
    }

    /**
     * Closes this output stream and releases any system resources associated with this stream.
     */
    @Override
    public void close() {
        flush();
        hasBeenClosed = true;
    }

    private byte[] extendBuffer(final byte[] buf, final int extensionSize) {
        final int newBufLength = buf.length + extensionSize;
        final byte[] newBuf = new byte[newBufLength];
        System.arraycopy(buf, 0, newBuf, 0, buf.length);
        return newBuf;
    }

}