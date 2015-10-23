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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.Resource.Accessor;
import com.persinity.common.invariant.Invariant;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author Doichin Yordanov
 */
public class ResourceTest {

    private static final class ResultAccessor extends Accessor<Connection, Integer> {
        ResultAccessor(final Connection resource) {
            super(resource, null);
        }

        @Override
        public Integer access(final Connection resource) throws Exception {
            return 2;
        }
    }

    ;

    private static final class ExceptionAccessor extends Accessor<Connection, Void> {

        protected ExceptionAccessor(final Connection resource) {
            super(resource, null);
        }

        @Override
        public Void access(final Connection resource) throws Exception {
            throw new IOException();
        }

    }

    private static final class ExceptionAccessorWithDescr extends Accessor<Connection, Void> {

        protected ExceptionAccessorWithDescr(final Connection resource, final String descr) {
            super(resource, descr);
        }

        @Override
        public Void access(final Connection resource) throws Exception {
            throw new IOException();
        }

    }

    /**
     * Test method for {@link com.persinity.common.Resource#access(com.persinity.common.Resource.Accessor)}.
     */
    @Test
    public void testAccess() {
        final Resource access = new Resource();

        // Check access with valid result
        final Connection niceConn = EasyMock.createNiceMock(Connection.class);
        EasyMock.replay(niceConn);
        final Integer result = access.access(new ResultAccessor(niceConn));
        Assert.assertThat(result, equalTo(2));

        // Check access with exception
        final Connection badConn = EasyMock.createStrictMock(Connection.class);
        try {
            badConn.close();
            EasyMock.expectLastCall();
        } catch (final SQLException e1) {
            Assert.fail(); // Mocked connection should not throw exceptions
        }
        EasyMock.replay(badConn);

        boolean catched = false;
        try {
            access.access(new ExceptionAccessor(badConn));
        } catch (final Exception e) {
            catched = true;
            Assert.assertThat(e, instanceOf(RuntimeException.class));
            final RuntimeException re = (RuntimeException) e;
            final Throwable cause = re.getCause();
            Assert.assertNotNull(cause);
            Assert.assertThat(cause, instanceOf(IOException.class));
            assertEquals(BADCONN_STRING, e.getMessage());
        }
        Assert.assertTrue(catched);

        // Check access with exception and explicit description
        EasyMock.reset(badConn);
        EasyMock.replay(badConn);
        catched = false;
        try {
            access.access(new ExceptionAccessorWithDescr(badConn, TEST_DESCRIPTION));
        } catch (final Exception e) {
            catched = true;
            Assert.assertThat(e, instanceOf(RuntimeException.class));
            final RuntimeException re = (RuntimeException) e;
            final Throwable cause = re.getCause();
            Assert.assertNotNull(cause);
            Assert.assertThat(cause, instanceOf(IOException.class));
            assertEquals(BADCONN_DESCR_STRING, e.getMessage());
        }
        Assert.assertTrue(catched);

    }

    /**
     * Test method for {@link com.persinity.common.Resource#accessAndAutoClose(com.persinity.common.Resource.Accessor)}.
     */
    @Test
    public void testAccessAndClose() {
        final Resource access = new Resource();

        // Check access with valid result
        final Connection niceConn = EasyMock.createNiceMock(Connection.class);
        try {
            niceConn.close();
        } catch (final SQLException e1) {
            Assert.fail(); // Mocked connection should not throw exceptions
        }
        EasyMock.expectLastCall();
        EasyMock.replay(niceConn);
        final Integer result = access.accessAndAutoClose(new ResultAccessor(niceConn));
        Assert.assertThat(result, equalTo(2));
        EasyMock.verify(niceConn);

        // Check access with exception
        final Connection badConn = EasyMock.createStrictMock(Connection.class);
        try {
            badConn.close();
        } catch (final SQLException e1) {
            Assert.fail(); // Mocked connection should not throw exceptions
        }
        EasyMock.expectLastCall();
        EasyMock.replay(badConn);

        boolean catched = false;
        try {
            access.accessAndAutoClose(new ExceptionAccessor(badConn));
        } catch (final Exception e) {
            catched = true;
            Assert.assertThat(e, instanceOf(RuntimeException.class));
            final RuntimeException re = (RuntimeException) e;
            final Throwable cause = re.getCause();
            Assert.assertNotNull(cause);
            Assert.assertThat(cause, instanceOf(IOException.class));
        }
        Assert.assertTrue(catched);
        EasyMock.verify(badConn);

    }

    @Test
    public void testClose() {
        final Resource testee = new Resource();

        final Connection conn = EasyMock.createStrictMock(Connection.class);
        try {
            conn.close();
        } catch (SQLException e) {
            // Mock should not raise exception
            Invariant.assertState(false);
        }
        EasyMock.expectLastCall();
        EasyMock.replay(conn);

        final Connection closedConn = testee.close(conn);

        EasyMock.verify(conn);
        assertEquals(conn, closedConn);
    }

    @Test
    public void testCloseFaultyResource() {
        final Resource testee = new Resource();

        final Connection conn = EasyMock.createStrictMock(Connection.class);
        try {
            conn.close();
        } catch (SQLException e) {
            // Mock should not raise exception
            Invariant.assertState(false);
        }
        final Throwable sqlException = new SQLException("test fault");
        EasyMock.expectLastCall().andThrow(sqlException);
        EasyMock.replay(conn);

        try {
            testee.close(conn);
        } catch (final RuntimeException e) {
            assertEquals(Resource.getExceptionDescriptionFor(conn, null), e.getMessage());
            assertEquals(e.getCause(), sqlException);
        }

        EasyMock.verify(conn);
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ResourceTest.class));
    private static final String BADCONN_STRING = "Access failed to EasyMock for interface java.sql.Connection";
    private static final String TEST_DESCRIPTION = "test description";
    private static final String BADCONN_DESCR_STRING = "Access failed to " + TEST_DESCRIPTION;
}
