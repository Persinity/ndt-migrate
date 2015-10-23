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