/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.logging;

import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMockSupport;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class LoggingRepeaterFuncTest extends EasyMockSupport {

    @Test
    public void testMsg() throws Exception {
        final Log4jLogger log = createMock(Log4jLogger.class);
        final LoggingRepeaterFunc<Integer> testee = new LoggingRepeaterFunc<>(log, "msg");

        log.info("msg");
        expectLastCall();

        replayAll();

        final Integer res = testee.apply(10);

        verifyAll();

        assertThat(res, is(10));
    }

    @Test
    public void testEmpty() throws Exception {
        final Log4jLogger log = createMock(Log4jLogger.class);
        final LoggingRepeaterFunc<Integer> testee = new LoggingRepeaterFunc<>(log, "");

        log.info("");
        expectLastCall();

        replayAll();

        final Integer res = testee.apply(10);

        verifyAll();

        assertThat(res, is(10));
    }

    @Test
    public void testMsgWithArgs() throws Exception {
        final Log4jLogger log = createMock(Log4jLogger.class);
        final LoggingRepeaterFunc<Integer> testee = new LoggingRepeaterFunc<>(log, "msg", "arg1");

        log.info("msg", "arg1");
        expectLastCall();

        replayAll();

        final Integer res = testee.apply(10);

        verifyAll();

        assertThat(res, is(10));
    }

    @Test(expected = NullPointerException.class)
    public void testLogNull() throws Exception {
        new LoggingRepeaterFunc<>(null, "msg");
    }

    @Test(expected = NullPointerException.class)
    public void testMsgNull() throws Exception {
        final Log4jLogger log = createMock(Log4jLogger.class);
        new LoggingRepeaterFunc<>(log, null);
    }
}