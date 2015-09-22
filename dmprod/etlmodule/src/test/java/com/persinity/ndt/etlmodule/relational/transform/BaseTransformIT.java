/**
 * Copyright (c) 2015 Persinity Inc.
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
