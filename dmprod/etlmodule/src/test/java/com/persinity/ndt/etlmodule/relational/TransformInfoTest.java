/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.RepeaterTupleFunc;
import com.persinity.ndt.transform.TupleFunc;

/**
 * @author Doichin Yordanov
 */
public class TransformInfoTest {

    @Test(expected = NullPointerException.class)
    public void testTransformInfoIllegalInput1() {
        final DirectedEdge<String, String> entityMapping = null;
        @SuppressWarnings("unchecked")
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final RelExtractFunc extractFunc = EasyMock.createNiceMock(RelExtractFunc.class);
        final TupleFunc transformFunc = null;
        final RelLoadFunc loadFunc = EasyMock.createNiceMock(RelLoadFunc.class);
        new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc, 8);
    }

    @Test(expected = NullPointerException.class)
    public void testTransformInfoIllegalInput2() {
        @SuppressWarnings("unchecked")
        final DirectedEdge<String, String> entityMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = null;
        final RelExtractFunc extractFunc = EasyMock.createNiceMock(RelExtractFunc.class);
        final TupleFunc transformFunc = null;
        final RelLoadFunc loadFunc = EasyMock.createNiceMock(RelLoadFunc.class);
        new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc, 8);
    }

    @Test(expected = NullPointerException.class)
    public void testTransformInfoIllegalInput3() {
        @SuppressWarnings("unchecked")
        final DirectedEdge<String, String> entityMapping = EasyMock.createNiceMock(DirectedEdge.class);
        @SuppressWarnings("unchecked")
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final RelExtractFunc extractFunc = null;
        final TupleFunc transformFunc = null;
        final RelLoadFunc loadFunc = EasyMock.createNiceMock(RelLoadFunc.class);
        new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc, 8);
    }

    @Test(expected = NullPointerException.class)
    public void testTransformInfoIllegalInput4() {
        @SuppressWarnings("unchecked")
        final DirectedEdge<String, String> entityMapping = EasyMock.createNiceMock(DirectedEdge.class);
        @SuppressWarnings("unchecked")
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final RelExtractFunc extractFunc = EasyMock.createNiceMock(RelExtractFunc.class);
        final TupleFunc transformFunc = null;
        final RelLoadFunc loadFunc = null;
        new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc, 8);
    }

    @Test
    public void testGetters() {
        @SuppressWarnings("unchecked")
        final DirectedEdge<String, String> entityMapping = EasyMock.createNiceMock(DirectedEdge.class);
        @SuppressWarnings("unchecked")
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final RelExtractFunc extractFunc = EasyMock.createNiceMock(RelExtractFunc.class);
        final TupleFunc transformFunc = EasyMock.createNiceMock(TupleFunc.class);
        final RelLoadFunc loadFunc = EasyMock.createNiceMock(RelLoadFunc.class);
        final TransformInfo testee = new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc,
                8);

        assertEquals(entityMapping, testee.getEntityMapping());
        assertEquals(colsMapping, testee.getColumnsMapping());
        assertEquals(extractFunc, testee.getExtractFunc());
        assertEquals(transformFunc, testee.getTransformFunc());
        assertEquals(loadFunc, testee.getLoadFunc());
    }

    @Test
    public void testEqualsObject() {
        @SuppressWarnings("unchecked")
        final DirectedEdge<String, String> entityMapping = EasyMock.createNiceMock(DirectedEdge.class);
        @SuppressWarnings("unchecked")
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = EasyMock.createNiceMock(DirectedEdge.class);
        final RelExtractFunc extractFunc = EasyMock.createNiceMock(RelExtractFunc.class);
        final TupleFunc transformFunc1 = EasyMock.createNiceMock(TupleFunc.class);
        final TupleFunc transformFunc2 = EasyMock.createNiceMock(RepeaterTupleFunc.class);
        final RelLoadFunc loadFunc = EasyMock.createNiceMock(RelLoadFunc.class);
        final TransformInfo ti11 = new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc1, loadFunc,
                8);
        final TransformInfo ti12 = new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc1, loadFunc,
                8);
        final TransformInfo ti21 = new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc2, loadFunc,
                8);
        final TransformInfo ti22 = new TransformInfo(entityMapping, colsMapping, extractFunc, transformFunc2, loadFunc,
                8);

        assertEquals(ti11, ti11);
        assertEquals(ti11.hashCode(), ti11.hashCode());
        assertEquals(ti11, ti12);
        assertEquals(ti12, ti11);
        assertEquals(ti11.hashCode(), ti12.hashCode());

        assertEquals(ti21, ti21);
        assertEquals(ti21.hashCode(), ti21.hashCode());
        assertEquals(ti21, ti22);
        assertEquals(ti22, ti21);
        assertEquals(ti21.hashCode(), ti22.hashCode());

        assertNotEquals(ti11, null);
        assertNotEquals(ti21, null);
        assertNotEquals(ti11, ti21);

    }

}
