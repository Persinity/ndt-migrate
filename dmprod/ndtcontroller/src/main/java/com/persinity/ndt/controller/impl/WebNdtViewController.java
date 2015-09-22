/**
 * Copyright (c) 2015 Persinity Inc.
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
