/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.transform;

import static java.util.Arrays.asList;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;

/**
 * @author dyordanov
 */
public class UpdateParamDmlFuncTest {

    @Test
    public void testApply() throws Exception {
        final String sql = "UPDATE emp SET ename = ?, sal = ? WHERE empid = ? AND deptid = ?";
        final List<Col> cols = asList(new Col("emp"), new Col("sal"), new Col("empid"), new Col("deptid"));
        final List<Col> idCols = asList(new Col("empid"), new Col("deptid"));
        final UpdateParamDmlFunc testee = new UpdateParamDmlFunc(sql, cols, idCols);
        final List<?> params = asList("Doichin", 1000, 1, 10);
        final List<?> paramsExpected = asList("Doichin", 1000, 1, 10, 1, 10);
        final RelDb db = EasyMock.createStrictMock(RelDb.class);
        EasyMock.expect(db.executePreparedDml(sql, paramsExpected)).andReturn(1);
        final DirectedEdge<RelDb, List<?>> input = new DirectedEdge<RelDb, List<?>>(db, params);

        EasyMock.replay(db);

        int actual = testee.apply(input);

        Assert.assertEquals(1, actual);
        EasyMock.verify(db);
    }
}