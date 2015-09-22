/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbdiff;

import java.util.Collection;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Schema diff generator interface.
 *
 * @author Ivan Dachev
 */
public interface SchemaDiffGenerator {
	/**
	 * Generates schema diff.
	 *
	 * @param dbSources   schemas to be used
	 * @param sqlStrategy AgentSqlStrategy to use
	 */
	Collection<TransformEntity> generateDiff(DirectedEdge<SchemaInfo, SchemaInfo> dbSources,
			AgentSqlStrategy sqlStrategy);
}
