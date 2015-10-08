/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static com.persinity.common.invariant.Invariant.assertArg;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author dyordanov
 */
public class KVStoreTest {

    @Test
    public void testRemove_NonExisting() throws Exception {
        final KVStore<Long, LongCloseable> testee = stubResourceStore();
        final LongCloseable longCloseableOne = new LongCloseable(1);
        final LongCloseable actual = testee.remove(1L);
        assertNull(actual);
        assertEquals(1, longCloseableOne.value());
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_NullValue() throws Exception {
        final KVStore<Long, LongCloseable> testee = stubResourceStore();
        testee.remove(null);
    }

    @Test
    public void testRemove_Failure() throws Exception {
        KVStore<Long, LongCloseable> testee = new KVStore<>(new Function<Long, LongCloseable>() {
            @Override
            public LongCloseable apply(final Long input) {
                return new FaultyLongCloseable(input.intValue());
            }
        }, new Function<LongCloseable, LongCloseable>() {
            @Override
            public LongCloseable apply(final LongCloseable input) {
                input.close();
                return input;
            }
        });
        final LongCloseable longCloseable = testee.get(1L);
        try {
            testee.remove(1L);
        } catch (RuntimeException e) {
            assertEquals("java.lang.RuntimeException: " + FaultyLongCloseable.EXPECTED, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testNewEntryGetKeySetRemove() throws Exception {
        final KVStore<Long, LongCloseable> testee = stubResourceStore();

        final LongCloseable longCloseableOne = testee.get(1L);
        assertEquals(1, longCloseableOne.value());
        final LongCloseable longCloseableTwo = testee.get(2L);
        assertEquals(2, longCloseableTwo.value());
        assertEquals(new HashSet<>(asList(1L, 2L)), testee.keySet());

        final LongCloseable actual = testee.remove(1L);
        assertNotNull(actual);
        assertEquals(0L, actual.value());
        assertEquals(longCloseableTwo, testee.get(2L));
        assertEquals(new HashSet<>(asList(2L)), testee.keySet());

        final LongCloseable actual2 = testee.remove(2L);
        assertNotNull(actual2);
        assertEquals(0L, actual2.value());
        assertEquals(Collections.emptySet(), testee.keySet());
    }

    @Test(expected = NullPointerException.class)
    public void testNewEntry_ForNull() throws Exception {
        final KVStore<Long, LongCloseable> testee = stubResourceStore();
        testee.get(null);
    }

    @Test
    public void testRemoveAll() throws Exception {
        final KVStore<Long, LongCloseable> testee = stubResourceStore();

        testee.get(1L);
        testee.get(2L);
        testee.get(3L);
        testee.get(4L);

        testee.removeAll(new HashSet<>(asList(1L)), null);
        assertEquals(new HashSet<>(asList(2L, 3L, 4L)), testee.keySet());

        testee.removeAll(new HashSet<>(asList(2L, 3L)), null);
        assertEquals(new HashSet<>(asList(4L)), testee.keySet());

        testee.removeAll(new HashSet<>(asList(1L, 2L, 3L)), null);
        assertEquals(new HashSet<>(asList(4L)), testee.keySet());

        testee.removeAll(new HashSet<>(asList(3L, 4L, 5L)), null);
        assertEquals(Collections.emptySet(), testee.keySet());

    }

    private KVStore<Long, LongCloseable> stubResourceStore() {
        final KVStore<Long, LongCloseable> testee = new KVStore<>(new Function<Long, LongCloseable>() {
            @Override
            public LongCloseable apply(final Long input) {
                return new LongCloseable(input.intValue());
            }
        }, new Function<LongCloseable, LongCloseable>() {
            @Override
            public LongCloseable apply(final LongCloseable input) {
                input.close();
                return input;
            }
        });
        return testee;
    }

    public static class LongCloseable implements Closeable {

        public LongCloseable(final long value) {
            assertArg(value > 0L);
            this.value = new AtomicLong(value);
        }

        @Override
        public void close() throws RuntimeException {
            log.debug("{} is closing in {}", this, Thread.currentThread());
            value.set(0L);
        }

        public long value() {
            return value.get();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof LongCloseable))
                return false;

            final LongCloseable that = (LongCloseable) o;

            return value.get() == that.value.get();
        }

        @Override
        public int hashCode() {
            return Long.valueOf(value.get()).hashCode();
        }

        @Override
        public String toString() {
            return "" + value.get();
        }

        private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LongCloseable.class));
        private AtomicLong value;
    }

    static class FaultyLongCloseable extends LongCloseable {

        public FaultyLongCloseable(final int value) {
            super(value);
        }

        @Override
        public void close() throws RuntimeException {
            throw new RuntimeException(EXPECTED);
        }

        public static final String EXPECTED = "Expected";
    }

}