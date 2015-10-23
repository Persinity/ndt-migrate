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
