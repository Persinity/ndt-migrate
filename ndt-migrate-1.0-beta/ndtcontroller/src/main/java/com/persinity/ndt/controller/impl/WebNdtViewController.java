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
package com.persinity.ndt.controller.impl;

import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.NdtControllerConfig;
import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.NdtFrontEndProtocol;
import com.persinity.ndt.controller.NdtViewController;

/**
 * Controller for the web front end.
 * <p/>
 * TODO the run here and the constructor should be removed.
 * The current NdtController impl will try to instantiate the view
 * with an empty constructor. So here in the WebNdtViewController
 * constructor we must create/get instance of NdtFrontEndProtocol
 * and start using it.
 *
 * @author Doichin Yordanov
 */
public class WebNdtViewController extends BaseNdtViewController implements NdtViewController {

    private final NdtFrontEndProtocol frontEnd;
    private final NdtController backEnd;

    public WebNdtViewController(NdtFrontEndProtocol frontEndProtocol) {
        frontEnd = frontEndProtocol;
        backEnd = NdtController.createFromConfig(NdtControllerConfig.DEFAULT_CONFIG_FILE_NAME);
    }

    @Override
    public void logNdtMessage(String msg) {
        frontEnd.sendNdtMessage(msg);
    }

    @Override
    public void setNdtStatusMessage(final String msg) {
        frontEnd.sendNdtStatusMessage(msg);
    }

    @Override
    public void setProgress(final boolean state) {
        // TODO impl in frontEnd
    }

    @Override
    protected void fire(NdtEvent event) {
        frontEnd.sendEvent(event.toString());
    }

    @Override
    public void run() {
        try {
            backEnd.run();
        } catch (final RuntimeException e) {
            throw e;
        } finally {
            backEnd.close();
        }
    }

}
