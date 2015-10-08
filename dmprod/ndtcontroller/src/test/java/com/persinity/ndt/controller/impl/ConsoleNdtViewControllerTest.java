/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.impl;

import static com.persinity.common.ThreadUtil.waitForCondition;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.setupCompleted;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.ConsoleProgressBar;
import com.persinity.common.ThreadUtil;
import com.persinity.ndt.controller.NdtEvent;

/**
 * @author Ivan Dachev
 */
public class ConsoleNdtViewControllerTest {
    @Before
    public void setUp() {
        releaseRead = false;
        inStream = new InputStream() {
            @Override
            public int read() throws IOException {
                if (inStreamReadDone) {
                    return '\n';
                }
                final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
                    @Override
                    public Boolean apply(final Void aVoid) {
                        return releaseRead;
                    }
                };
                if (!waitForCondition(condition, TIMEOUT_READ_RELEASE_MS)) {
                    fail("Timeout wait read release");
                }
                inStreamReadDone = true;
                return 'Y';
            }
        };
        origIn = System.in;
        System.setIn(inStream);

        outStream = new ByteArrayOutputStream();
        outPStream = new PrintStream(outStream);
        origOut = System.out;
        System.setOut(outPStream);

        testee = new ConsoleNdtViewController();
    }

    @After
    public void tearDown() {
        if (origIn != null) {
            System.setIn(origIn);
            origIn = null;
        }
        if (origOut != null) {
            System.setOut(origOut);
            origOut = null;
        }
    }

    @Test
    public void testLogNdtMessage() {
        outPStream.flush();
        outStream.reset();

        testee.logNdtMessage("test");

        outPStream.flush();
        assertTrue(outStream.toString().trim().endsWith("test"));
    }

    @Test
    public void testSetNdtStatusMessage() {
        outPStream.flush();
        outStream.reset();

        testee.setNdtStatusMessage("test");

        outPStream.flush();
        assertTrue(outStream.toString().endsWith("test\r"));

        outPStream.flush();
        outStream.reset();

        testee.logNdtMessage("ndtTest");
        testee.setNdtStatusMessage("test");

        outPStream.flush();
        assertTrue(outStream.toString().endsWith("test\r"));
    }

    @Test
    public void testFire() {
        final Thread th = new Thread() {
            @Override
            public void run() {
                ThreadUtil.sleep(500);
                releaseRead = true;
            }
        };

        inStreamReadDone = false;
        th.start();
        testee.sendBlockingEvent(new NdtEvent(setupCompleted));
        assertTrue(inStreamReadDone);
    }

    @Test
    public void testSetProgress() {
        final ConsoleProgressBar progressBar = createStrictMock(ConsoleProgressBar.class);
        progressBar.start();
        expectLastCall();
        progressBar.stop();
        expectLastCall();
        progressBar.start();
        expectLastCall();

        replay(progressBar);
        final ConsoleNdtViewController testee1 = new ConsoleNdtViewController(
                new BufferedReader(new InputStreamReader(System.in)), progressBar);

        testee1.setProgress(true);
        testee1.setProgress(false);
        testee1.setProgress(true);

        verify(progressBar);
    }

    private static final long TIMEOUT_READ_RELEASE_MS = 5000;
    private boolean releaseRead;
    private boolean inStreamReadDone;
    private InputStream inStream;
    private ConsoleNdtViewController testee;
    private ByteArrayOutputStream outStream;
    private PrintStream outPStream;
    private InputStream origIn;
    private PrintStream origOut;
}