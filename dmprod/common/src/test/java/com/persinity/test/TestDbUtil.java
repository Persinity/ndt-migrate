/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;

import com.persinity.common.db.RelDb;

/**
 * TODO refactor to use instead of TestDm duplicate methods.
 *
 * @author Ivan Dachev
 */
public class TestDbUtil {
    /**
     * @param db1
     * @param db2
     * @param srcQry
     * @param dstQry
     * @return number of compared results
     */
    public static int compareRs(final RelDb db1, final RelDb db2, final String srcQry, final String dstQry) {
        final Iterator<Map<String, Object>> srcRecs = db1.executeQuery(srcQry);
        final Iterator<Map<String, Object>> dstRecs = db2.executeQuery(dstQry);
        int compared = 0;
        while (srcRecs.hasNext()) {
            final Map<String, Object> srcRec = srcRecs.next();
            assertTrue(dstRecs.hasNext());
            final Map<String, Object> dstRec = dstRecs.next();
            Assert.assertEquals(srcRec, dstRec);
            compared++;
        }
        assertFalse(dstRecs.hasNext());
        return compared;
    }

    /**
     * @param db
     * @param qry
     */
    public static void assertEmptyRs(final RelDb db, final String qry) {
        final Iterator<Map<String, Object>> recs = db.executeQuery(qry);
        assertFalse(recs.hasNext());
    }

    /**
     * @param db
     * @param qry
     */
    public static void assertNotEmptyRs(final RelDb db, final String qry) {
        final Iterator<Map<String, Object>> recs = db.executeQuery(qry);
        assertTrue(recs.hasNext());
    }
}
