/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.junit.Before;

import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.NdtControllerConfig;
import com.persinity.ndt.controller.NdtViewController;

/**
 * @author Ivan Dachev
 */
public abstract class NdtStepBaseTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {
        ctx = new HashMap<>();
        ndtController = createMock(NdtController.class);
        ctx.put(NdtController.class, ndtController);
        ndtControllerConfig = createMock(NdtControllerConfig.class);
        expect(ndtController.getConfig()).andStubReturn(ndtControllerConfig);
        ndtControllerView = createMock(NdtViewController.class);
        expect(ndtController.getView()).andStubReturn(ndtControllerView);
    }

    protected Map<Object, Object> ctx;
    protected NdtController ndtController;
    protected NdtControllerConfig ndtControllerConfig;
    protected NdtViewController ndtControllerView;
}