/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

/**
 * @author Ivan Dachev
 */
public class TestNdtControllerUtil {

    /**
     * @param ndtController
     *         to create TestNdtUtil
     * @return
     */
    public static TestNdtUtil createTestNdtUtil(final NdtController ndtController, final boolean srcAppOnly) {
        return new TestNdtUtil(ndtController.getRelDbPoolFactory(), ndtController.getSqlStrategy());
    }

    public static final boolean TEST_NDT_UTIL_ALL = false;
    public static final boolean TEST_NDT_UTIL_SRC_APP_ONLY = true;
}
