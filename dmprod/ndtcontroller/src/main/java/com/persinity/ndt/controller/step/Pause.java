/**
 * Copyright (c) 2015 Persinity Inc.
 */
/**
 *
 */
package com.persinity.ndt.controller.step;

import static com.persinity.ndt.controller.NdtViewController.PROGRESS_OFF;

import java.util.Map;

import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.NdtViewController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;

/**
 * Pauses until key is hit
 *
 * @author Doichin Yordanov
 */
public class Pause extends BaseStep implements Step {

    public Pause(Step prev, int delaySecs, NdtEvent fireEvent, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
        this.fireEvent = fireEvent;
    }

    @Override
    protected void work() {
        final NdtViewController view = getController().getView();
        view.setProgress(PROGRESS_OFF);
        view.sendBlockingEvent(fireEvent);
    }

    private final NdtEvent fireEvent;
}
