/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

/**
 * Events raised by the NDT controller.
 *
 * @author Doichin Yordanov
 */
public class NdtEvent {
    // TODO revise these and switch to upper case
    public enum NdtEventType {
        bootstrapStarted,
        bootstrapCompleted,
        setupStarted,
        setupCompleted,                // NDT binaries installed, Source and target app URLs set
        ndtUpgradeStarted,
        mountingNdtStarted,
        mountingNdtCompleted,        // NDT src/trg db logic installed
        initialTransferStarted,        // Bulk transfer starts
        initialTransferCompleted,
        consistentTargetStarted,    // Applying deltas during bulk transfer and enabling trg integrity
        consistentTarget,            // Src can be retired
        deltaTransferStarted,        // Keeping trg up to sync with src
        deltaTransferCompleted,        // Src retired
        unmountingNdtStarted,
        unmountingNdtCompleted,        // NDT src/trg db logic deinstalled
        ndtUpgradeCompleted
    }

    public NdtEvent(final NdtEventType type) {
        this(type, "");
    }

    public NdtEvent(final NdtEventType type, final String message) {
        this.type = type;
        this.message = message;
    }

    public NdtEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    private final NdtEventType type;
    private final String message;
}
