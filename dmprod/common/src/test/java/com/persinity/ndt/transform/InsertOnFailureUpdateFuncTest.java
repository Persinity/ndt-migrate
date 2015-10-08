/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.OracleSqlStrategy;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivan Dachev
 */
public class InsertOnFailureUpdateFuncTest extends EasyMockSupport {

	@Test
	public void testApply() throws Exception {
		final String sqlI = "sqlInsert";
		final String sqlU = "sqlUpdate";
		Col col1 = new Col("col1", "name", false);
		Col col2 = new Col("col2Id", "id", false);
		Col col3 = new Col("col3Id", "id", true);
		final List<Col> cols = Arrays.asList(col1, col2, col3);
		final List<Col> idCols = Arrays.asList(col2, col3);
		final InsertOnFailureUpdateFunc f = new InsertOnFailureUpdateFunc(sqlI, sqlU, cols, idCols);

		final RelDb db = createMock(RelDb.class);

		final List<?> input = Arrays.asList("Ivan", "1", "2");
		expect(db.executePreparedDml(sqlI, input)).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() throws Throwable {
				throw new RuntimeException(new SQLException("dup keys", "231", 1));
			}
		});
		expect(db.getSqlStrategy()).andStubReturn(new OracleSqlStrategy());

        final List<?> inputUpdate = Arrays.asList("Ivan", "1", "2", "1", "2");
		expect(db.executePreparedDml(sqlU, inputUpdate)).andReturn(1);

		replayAll();

		final Integer res = f.apply(new DirectedEdge<RelDb, List<?>>(db, input));

		verifyAll();

		assertThat(res, is(1));
	}
}