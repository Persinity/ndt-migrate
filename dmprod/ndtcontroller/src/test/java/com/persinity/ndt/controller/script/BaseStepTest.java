/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.persinity.ndt.controller.NdtController;

/**
 * Unit test for {@link BaseStep}
 *
 * @author Doichin Yordanov
 */
public class BaseStepTest {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        ctx = Collections.synchronizedMap(new HashMap<>());
        ctx.put(ORDER, Collections.synchronizedList(new LinkedList<Long>()));

        controller = EasyMock.createNiceMock(NdtController.class);
        ctx.put(NdtController.class, controller);
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun() {
        final StepStub step1 = new StepStub(null, Step.NO_DELAY, ctx);
        final StepStub step2 = new StepStub(step1, Step.NO_DELAY, ctx);
        final Thread t1 = new Thread(step1);
        final Thread t2 = new Thread(step2);

        t2.start();
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
        }
        t1.start();

        while (!step2.isFinished()) {
            step2.waitToFinish(500);
        }

        assertFalse(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertFalse(step1.isFailed());
        assertThat(step1.getFailedException(), nullValue());
        step1.assertStepDoneResult();

        assertFalse(step2.isStopRequested());
        assertTrue(step2.isFinished());
        assertFalse(step2.isFailed());
        assertThat(step2.getFailedException(), nullValue());
        step2.assertStepDoneResult();
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun_Stop_OnWork() {
        final StepStopStub step1 = new StepStopStub(null, ctx);
        final Thread t1 = new Thread(step1);
        t1.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                step1.sigStop();
            }
        }.start();

        while (!step1.isFinished()) {
            step1.waitToFinish(500);
        }

        assertTrue(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertFalse(step1.isFailed());
        assertThat(step1.getFailedException(), nullValue());
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun_Stop_OnSchedule() {
        final StepLongStub step1 = new StepLongStub(null, ctx);
        final StepStub step2 = new StepStub(step1, Step.NO_DELAY, ctx);
        final Thread t1 = new Thread(step1);
        final Thread t2 = new Thread(step2);

        t2.start();
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
        }
        t1.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                step2.sigStop();
            }
        }.start();

        while (!step2.isFinished()) {
            step2.waitToFinish(500);
        }

        // stop step1 no need to block/check there
        t1.interrupt();

        assertTrue(step2.isStopRequested());
        assertTrue(step2.isFinished());
        assertFalse(step2.isFailed());
        assertThat(step2.getFailedException(), nullValue());
        step2.assertStepNotDoneResult();
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun_Fail() {
        final StepFailStub step1 = new StepFailStub(null, ctx);
        final Thread t1 = new Thread(step1);
        t1.start();

        while (!step1.isFinished()) {
            step1.waitToFinish(500);
        }

        assertFalse(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertTrue(step1.isFailed());
        assertThat(step1.getFailedException(), notNullValue());

        final RuntimeException e = step1.getFailedException();
        assertThat(e.getMessage(), is("Expected"));
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun_Interrupted_OnWork() {
        final StepLongStub step1 = new StepLongStub(null, ctx);
        final Thread t1 = new Thread(step1);
        t1.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t1.interrupt();
            }
        }.start();

        while (!step1.isFinished()) {
            step1.waitToFinish(500);
        }

        assertFalse(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertTrue(step1.isFailed());
        assertThat(step1.getFailedException(), notNullValue());

        final RuntimeException e = step1.getFailedException();
        assertThat(e.getCause(), instanceOf(InterruptedException.class));
    }

    /**
     * Test method for {@link BaseStep#run()}
     */
    @Test
    public void testRun_Interrupted_OnSchedule() {
        final StepLongStub step1 = new StepLongStub(null, ctx);
        final StepStub step2 = new StepStub(step1, Step.NO_DELAY, ctx);
        final Thread t1 = new Thread(step1);
        final Thread t2 = new Thread(step2);

        t2.start();
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
        }
        t1.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t2.interrupt();
            }
        }.start();

        while (!step2.isFinished()) {
            step2.waitToFinish(500);
        }

        // stop step1 no need to block/check there
        t1.interrupt();

        assertFalse(step2.isStopRequested());
        assertTrue(step2.isFinished());
        assertTrue(step2.isFailed());
        assertThat(step2.getFailedException(), notNullValue());

        final RuntimeException e = step2.getFailedException();
        assertThat(e.getCause(), instanceOf(InterruptedException.class));
    }

    /**
     * Test method for {@link BaseStep#getController()}
     */
    @Test
    public void testGetController() {
        final StepStub step1 = new StepStub(null, Step.NO_DELAY, ctx);
        assertThat(step1.getController(), is(controller));
    }

    private Map<Object, Object> ctx;
    private static final String ORDER = "order";
    private NdtController controller = null;
}
