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
