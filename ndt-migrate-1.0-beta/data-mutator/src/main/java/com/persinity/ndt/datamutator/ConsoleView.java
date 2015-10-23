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
package com.persinity.ndt.datamutator;

import static com.persinity.common.invariant.Invariant.notNull;

import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;

import com.persinity.common.ConsoleProgressBar;
import com.persinity.common.logging.Log4jLogger;
import jline.console.ConsoleReader;

/**
 * Implements console view with commands.
 *
 * @author Ivan Dachev
 */
public class ConsoleView extends Thread {

    /**
     * @param dm
     *         {@link DataMutator} to issue commands to
     * @param consoleReader
     * @param quite
     */
    public ConsoleView(final DataMutator dm, final Reader consoleReader, final boolean quite) {
        notNull(dm);

        this.dm = dm;
        this.consoleReader = consoleReader;
        this.quite = quite;

        consoleProgressBar = new ConsoleProgressBar();

        setDaemon(true);
    }

    /**
     * @param msg
     *         to log on the console view
     */
    public void logMsg(final String msg) {
        log.info(msg);
        if (lastMsgWasStatus) {
            println();
            lastMsgWasStatus = false;
        }
        println();
        println(msg);
    }

    /**
     * @param msg
     *         status to log on the console view, will overwrite previous status
     */
    public void logStatus(String msg) {
        lastMsgWasStatus = true;
        // pad with spaces to clear the console line from previous status
        if (msg.length() < MAX_CONSOLE_LINE_CHARS - 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append(msg);
            while (sb.length() < MAX_CONSOLE_LINE_CHARS - 1) {
                sb.append(' ');
            }
            msg = sb.toString();
        }
        print(msg + '\r');
    }

    /**
     * @return true if last output msg was a status
     */
    public boolean isLastMsgWasStatus() {
        return lastMsgWasStatus;
    }

    /**
     * @param state
     */
    public void setProgress(final boolean state) {
        if (!quite) {
            if (state) {
                consoleProgressBar.start();
            } else {
                consoleProgressBar.stop();
                println();
            }
        }
    }

    /**
     * @param msg
     *         to confirm
     * @return true if message was confirmed
     */
    public boolean confirm(final String msg) {
        if (quite) {
            log.info("Confirm quietly: {}", msg);
            return true;
        }
        boolean res;
        while (true) {
            logMsg(msg);
            print("Press \"Y\" to confirm or \"N\" to cancel: ");
            final String c = readChar();
            if (c.equals("y")) {
                res = true;
                break;
            } else if (c.equals("n")) {
                res = false;
                break;
            }
        }
        println();
        return res;
    }

    @Override
    public void run() {
        if (consoleReader == null) {
            log.info("Console reader not supported, skip console commands.");
            return;
        }
        dumpUsage();
        while (true) {
            try {
                switch (readChar()) {
                case CMD_CONNECTIONS_UP:
                    dm.connectionsUp();
                    break;
                case CMD_CONNECTIONS_DOWN:
                    dm.connectionsDown();
                    break;
                case CMD_TRANSACTION_DELAY_UP:
                    dm.transactionDelayUp();
                    break;
                case CMD_TRANSACTION_DELAY_DOWN:
                    dm.transactionDelayDown();
                    break;
                case CMD_TRANSACTION_RECORDS_UP:
                    dm.transactionRecordsUp();
                    break;
                case CMD_TRANSACTION_RECORDS_DOWN:
                    dm.transactionRecordsDown();
                    break;
                case CMD_PAUSE:
                    dm.pause();
                    break;
                case CMD_RESUME:
                    dm.resume();
                    break;
                case CMD_CLEANUP:
                    dm.cleanup();
                    break;
                case CMD_RESET:
                    dm.reset();
                    break;
                case CMD_STOP:
                    if (dm.stop()) {
                        return;
                    }
                    break;
                case "\n":
                case "\r":
                    dumpUsage();
                    break;
                }
            } catch (Exception e) {
                log.error(e, "Console processing failed");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    private void print(final String msg) {
        if (!quite) {
            System.out.print(msg);
        }
    }

    private void println() {
        if (!quite) {
            System.out.println();
        }
    }

    private void println(final String msg) {
        if (!quite) {
            System.out.println(msg);
        }
    }

    private String readChar() {
        final int c;
        try {
            c = consoleReader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String lc = String.valueOf((char) c).toLowerCase();
        return lc;
    }

    /**
     * @return
     */
    public static Reader openConsoleReader() {
        Reader consoleReader;
        try {
            final ConsoleReader reader = new ConsoleReader();
            consoleReader = new Reader() {
                @Override
                public int read(final char[] cbuf, final int off, final int len) throws IOException {
                    cbuf[off] = (char) reader.readCharacter();
                    return 1;
                }

                @Override
                public void close() throws IOException {
                    reader.shutdown();
                }
            };
        } catch (IOException e) {
            log.error(e, "Failed to open {}, fallback to System.console", ConsoleReader.class);
            consoleReader = null;
        }

        if (consoleReader == null) {
            final Console console = System.console();
            if (console != null) {
                consoleReader = console.reader();
            } else {
                consoleReader = new InputStreamReader(System.in);
            }
        }
        return consoleReader;
    }

    private void dumpUsage() {
        logMsg("Console commands:\n" + "\t\"1/2\"   - Decrease/Increase connections\n" +
                "\t\"3/4\"   - Decrease/Increase transaction delay by 50 ms\n" +
                "\t\"5/6\"   - Decrease/Increase records per transaction\n" +
                "\t\"P\"     - Pause all connections\n" +
                "\t\"R\"     - Resume all connections\n" +
                "\t\"C\"     - Cleanup all records in tables\n" +
                "\t\"X\"     - Reset all tables\n" +
                "\t\"Q\"     - Stop all connections and exit\n" +
                "\t\"Enter\" - Print this help");
    }

    private static final String CMD_CONNECTIONS_DOWN = "1";
    private static final String CMD_CONNECTIONS_UP = "2";
    private static final String CMD_TRANSACTION_DELAY_DOWN = "3";
    private static final String CMD_TRANSACTION_DELAY_UP = "4";
    private static final String CMD_TRANSACTION_RECORDS_DOWN = "5";
    private static final String CMD_TRANSACTION_RECORDS_UP = "6";
    private static final String CMD_PAUSE = "p";
    private static final String CMD_RESUME = "r";
    private static final String CMD_CLEANUP = "c";
    private static final String CMD_RESET = "x";
    private static final String CMD_STOP = "q";

    private static final int MAX_CONSOLE_LINE_CHARS = 80;

    private final DataMutator dm;
    private final Reader consoleReader;
    private final ConsoleProgressBar consoleProgressBar;
    private final boolean quite;

    private boolean lastMsgWasStatus;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ConsoleView.class));
}
