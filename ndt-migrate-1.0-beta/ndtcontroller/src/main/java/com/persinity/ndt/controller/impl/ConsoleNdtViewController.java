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
package com.persinity.ndt.controller.impl;

import static com.persinity.common.invariant.Invariant.notNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.persinity.common.ConsoleProgressBar;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.NdtViewController;

/**
 * View controller that interacts with std::out/in for NDT events/messages and with a log file for messages from the
 * monitored application.
 *
 * @author Doichin Yordanov
 */
public class ConsoleNdtViewController extends BaseNdtViewController implements NdtViewController {

    public ConsoleNdtViewController() {
        this(new BufferedReader(new InputStreamReader(System.in)), new ConsoleProgressBar());
    }

    ConsoleNdtViewController(BufferedReader consoleReader, ConsoleProgressBar progressBar) {
        notNull(consoleReader);
        notNull(progressBar);

        this.consoleReader = consoleReader;
        this.consoleProgressBar = progressBar;
    }

    @Override
    public void logNdtMessage(String msg) {
        log.info(msg.trim());
        System.out.println();
        System.out.println(msg);
        needStatusNewLine = true;
    }

    @Override
    public void setNdtStatusMessage(final String msg) {
        log.info(msg.trim());
        if (needStatusNewLine) {
            System.out.println();
            needStatusNewLine = false;
        }
        System.out.print(msg.trim() + '\r');
    }

    @Override
    public void setProgress(final boolean state) {
        if (state) {
            consoleProgressBar.start();
        } else {
            consoleProgressBar.stop();
            System.out.println();
        }
    }

    @Override
    final protected void fire(NdtEvent event) {
        if (BLOCKING_EVENTS.contains(event.getType())) {
            while (!isClosed()) {
                logNdtMessage(event.getMessage());
                System.out.print(CONFIRM_MSG);
                final String input;
                try {
                    input = consoleReader.readLine().trim();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                if (input.equalsIgnoreCase("y")) {
                    System.out.println();
                    break;
                }
            }
            receiveAck();
        } else {
            log.info("Skip event: {} msg: {}", event, event.getMessage());
        }
    }

    @Override
    public void run() {
    }

    private final String CONFIRM_MSG = "Enter \"Y\" to continue: ";
    private final BufferedReader consoleReader;
    private final ConsoleProgressBar consoleProgressBar;

    private boolean needStatusNewLine;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ConsoleNdtViewController.class));
}
