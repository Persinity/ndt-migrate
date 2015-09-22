/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbdiff;

import java.util.Collection;

/**
 * Provides a store of TransformEntities.
 *
 * @author Ivan Dachev
 */
public interface TransformEntityStore {
	/**
	 * @return TransformEntities loaded from the store
	 */
	Collection<TransformEntity> loadTransformEntities();

	/**
	 * @return TransformEntities loaded from the store
	 */
	void saveTransformEntities(Collection<TransformEntity> transformEntities);
}
