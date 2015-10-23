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
package com.persinity.ndt.etlmodule.relational.transform;

import org.junit.After;
import org.junit.Before;

import com.persinity.ndt.etlmodule.relational.migrate.BaseMigrateIT;

/**
 * Base transform integration test.
 *
 * @author Ivan Dachev
 */
public abstract class BaseTransformIT extends BaseMigrateIT {

    @Before
    public void setUp() {
        super.setUp();
        testTransform = new TestTransform(getRelDbPoolFactory(), getWindowSize(), testMigrate);
    }

    @After
    public void tearDown() {
        testTransform.close();
        super.tearDown();
    }

    public TestTransform getTestTransform() {
        return testTransform;
    }

    private TestTransform testTransform;
}
