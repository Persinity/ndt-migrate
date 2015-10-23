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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.waitForCondition;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtEvent;

/**
 * @author Ivan Dachev
 */
public class StubNdtViewController extends BaseNdtViewController {

    @Override
    protected void fire(final NdtEvent event) {
        final String msg = event.getMessage();
        if (msg.length() > 0) {
            log.info(msg);
            System.out.println(msg);
            allNdtMsg += msg;
        }
    }

    @Override
    public void logNdtMessage(final String msg) {
        log.info(msg);
        System.out.println(msg);
        allNdtMsg += msg;
    }

    @Override
    public void setNdtStatusMessage(final String msg) {
        log.info(msg);
        System.out.println(msg);
        allNdtMsg += msg;
    }

    @Override
    public void setProgress(final boolean state) {
        log.debug("Set progress: {}", state);
    }

    @Override
    public void run() {

    }

    public void waitForPauseMsgAndReleaseIt(final String pauseMsg) {
        waitForMsg(pauseMsg);
        receiveAck();
    }

    public void waitForMsg(final String msgToWaitFor) {
        log.debug("Wait for msg: {}", msgToWaitFor);

        final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return allNdtMsg.contains(msgToWaitFor);
            }
        };

        if (!waitForCondition(condition, WAIT_TIMEOUT_SECONDS * 1000L)) {
            fail(format("Timeout waiting for msg: {}", msgToWaitFor));
        } else {
            log.debug("Wait for msg: {} DONE", msgToWaitFor);
        }
    }

    private String allNdtMsg = "";

    private static final int WAIT_TIMEOUT_SECONDS = 300;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(StubNdtViewController.class));
}
