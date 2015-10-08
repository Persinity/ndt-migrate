/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

import java.util.Arrays;
import java.util.List;

import com.persinity.ndt.controller.NdtEvent.NdtEventType;

/**
 * Abstracts NDT view controller
 * <p/>
 * TODO make MVC refactor here to log messages by ID and
 * Move all the message string to localization that can be shared between view controllers.
 * Refactor pause/blocking events to work only by ID and the consoel/web impls to decide what to show and when.
 *
 * @author Doichin Yordanov
 */
public interface NdtViewController {
    /**
     * Events that require ACK
     */
    List<NdtEventType> BLOCKING_EVENTS = Arrays
            .asList(new NdtEventType[] { NdtEventType.bootstrapCompleted, // Press key to setup NDT...
                    NdtEventType.setupCompleted, // Press key to start NDT Migrate...
                    NdtEventType.initialTransferCompleted, // Press key when initial transfer is completed...
                    NdtEventType.consistentTarget, // Press key to retire legacy...
                    NdtEventType.deltaTransferCompleted, // Press key when legacy is retired.
            });

    /**
     * Progress ON
     */
    boolean PROGRESS_ON = true;

    /**
     * Progress OFF
     */
    boolean PROGRESS_OFF = false;

    /**
     * Displays a message from NDT in the UI
     *
     * @param msg
     */
    void logNdtMessage(String msg);

    /**
     * Set a status message.
     *
     * @param msg
     */
    void setNdtStatusMessage(String msg);

    /**
     * @param state
     *         PROGRESS_ON/PROGRESS_OFF
     */
    void setProgress(boolean state);

    /**
     * Handles user input (Next-Next style) by calling the appropriate Ndt controller method
     */
    void receiveAck();

    /**
     * Sends a {@link NdtEvent}
     *
     * @param event
     */
    void sendEvent(NdtEvent event);

    /**
     * Sends a blocking {@link NdtEvent}, which is ACK on fire of recieveEvent
     *
     * @param event
     */
    void sendBlockingEvent(NdtEvent event);

    /**
     * Starts the controller.
     */
    void run();

    /**
     * Close the controller.
     */
    void close();
}
