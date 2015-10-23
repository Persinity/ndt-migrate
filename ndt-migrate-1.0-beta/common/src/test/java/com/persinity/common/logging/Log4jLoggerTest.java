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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.apache.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class Log4jLoggerTest extends EasyMockSupport {
    private Logger log4j;

    @Before
    public void setUp() {
        log4j = createMock(Logger.class);
    }

    @Test
    public void testInfo() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);

        expect(log4j.isInfoEnabled()).andStubReturn(true);

        log4j.info("");
        expectLastCall();
        log4j.info("1");
        expectLastCall();
        log4j.info("2");
        expectLastCall();

        replayAll();

        log.info("");
        log.info("1");
        log.info("{}", "2");

        verifyAll();
    }

    @Test
    public void testInfo_Disabled() throws Exception {
        Logger log4j = createMock(Logger.class);
        Log4jLogger log = new Log4jLogger(log4j);

        expect(log4j.isInfoEnabled()).andStubReturn(false);

        replayAll();

        log.info("");
        log.info("1");
        log.info("{}", "2");

        verifyAll();
    }

    @Test
    public void testInfo_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });

        expect(log4j.isInfoEnabled()).andStubReturn(true);

        log4j.info("prefix ");
        expectLastCall();
        log4j.info("prefix 1");
        expectLastCall();
        log4j.info("prefix 2");
        expectLastCall();

        replayAll();

        log.info("");
        log.info("1");
        log.info("{}", "2");

        verifyAll();
    }

    @Test
    public void testError() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);

        log4j.error("");
        expectLastCall();
        log4j.error("1");
        expectLastCall();
        log4j.error("2");
        expectLastCall();

        replayAll();

        log.error("");
        log.error("1");
        log.error("{}", "2");

        verifyAll();
    }

    @Test
    public void testError_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });

        log4j.error("prefix ");
        expectLastCall();
        log4j.error("prefix 1");
        expectLastCall();
        log4j.error("prefix 2");
        expectLastCall();

        replayAll();

        log.error("");
        log.error("1");
        log.error("{}", "2");

        verifyAll();
    }

    @Test
    public void testErrorThrowable() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);
        Throwable t = new Throwable();

        log4j.error("", t);
        expectLastCall();
        log4j.error("1", t);
        expectLastCall();
        log4j.error("2", t);
        expectLastCall();

        replayAll();

        log.error(t, "");
        log.error(t, "1");
        log.error(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testErrorThrowable_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });
        Throwable t = new Throwable();

        log4j.error("prefix ", t);
        expectLastCall();
        log4j.error("prefix 1", t);
        expectLastCall();
        log4j.error("prefix 2", t);
        expectLastCall();

        replayAll();

        log.error(t, "");
        log.error(t, "1");
        log.error(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testWarning() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);

        log4j.warn("");
        expectLastCall();
        log4j.warn("1");
        expectLastCall();
        log4j.warn("2");
        expectLastCall();

        replayAll();

        log.warn("");
        log.warn("1");
        log.warning("{}", "2");

        verifyAll();
    }

    @Test
    public void testWarning_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });

        log4j.warn("prefix ");
        expectLastCall();
        log4j.warn("prefix 1");
        expectLastCall();
        log4j.warn("prefix 2");
        expectLastCall();

        replayAll();

        log.warn("");
        log.warning("1");
        log.warn("{}", "2");

        verifyAll();
    }

    @Test
    public void testWarningThrowable() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);
        Throwable t = new Throwable();

        log4j.warn("", t);
        expectLastCall();
        log4j.warn("1", t);
        expectLastCall();
        log4j.warn("2", t);
        expectLastCall();

        replayAll();

        log.warning(t, "");
        log.warn(t, "1");
        log.warn(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testWarningThrowable_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });
        Throwable t = new Throwable();

        log4j.warn("prefix ", t);
        expectLastCall();
        log4j.warn("prefix 1", t);
        expectLastCall();
        log4j.warn("prefix 2", t);
        expectLastCall();

        replayAll();

        log.warn(t, "");
        log.warning(t, "1");
        log.warn(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testDebug() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);

        expect(log4j.isDebugEnabled()).andStubReturn(true);

        log4j.debug("");
        expectLastCall();
        log4j.debug("1");
        expectLastCall();
        log4j.debug("2");
        expectLastCall();

        replayAll();

        log.debug("");
        log.debug("1");
        log.debug("{}", "2");

        verifyAll();
    }

    @Test
    public void testDebug_Disabled() throws Exception {
        Logger log4j = createMock(Logger.class);
        Log4jLogger log = new Log4jLogger(log4j);

        expect(log4j.isDebugEnabled()).andStubReturn(false);

        replayAll();

        log.debug("");
        log.debug("1");
        log.debug("{}", "2");

        verifyAll();
    }

    @Test
    public void testDebug_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });

        expect(log4j.isDebugEnabled()).andStubReturn(true);

        log4j.debug("prefix ");
        expectLastCall();
        log4j.debug("prefix 1");
        expectLastCall();
        log4j.debug("prefix 2");
        expectLastCall();

        replayAll();

        log.debug("");
        log.debug("1");
        log.debug("{}", "2");

        verifyAll();
    }

    @Test
    public void testDebugThrowable() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);
        Throwable t = new Throwable();

        expect(log4j.isDebugEnabled()).andStubReturn(true);

        log4j.debug("", t);
        expectLastCall();
        log4j.debug("1", t);
        expectLastCall();
        log4j.debug("2", t);
        expectLastCall();

        replayAll();

        log.debug(t, "");
        log.debug(t, "1");
        log.debug(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testDebugThrowable_Context() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j, new ContextProvider() {
            @Override
            public void appendContext(StringBuilder sb) {
                sb.append("prefix ");
            }
        });
        Throwable t = new Throwable();

        expect(log4j.isDebugEnabled()).andStubReturn(true);

        log4j.debug("prefix ", t);
        expectLastCall();
        log4j.debug("prefix 1", t);
        expectLastCall();
        log4j.debug("prefix 2", t);
        expectLastCall();

        replayAll();

        log.debug(t, "");
        log.debug(t, "1");
        log.debug(t, "{}", "2");

        verifyAll();
    }

    @Test
    public void testDebugThrowable_Disabled() throws Exception {
        Log4jLogger log = new Log4jLogger(log4j);
        Throwable t = new Throwable();

        expect(log4j.isDebugEnabled()).andStubReturn(false);

        replayAll();

        log.debug(t, "");
        log.debug(t, "1");
        log.debug(t, "{}", "2");

        verifyAll();
    }
}