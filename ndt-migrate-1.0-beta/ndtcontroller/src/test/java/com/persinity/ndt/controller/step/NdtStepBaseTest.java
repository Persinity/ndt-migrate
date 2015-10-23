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