/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

/**
 * Protocol for communication with the front-end
 *
 * @author Doichin Yordanov
 */
public interface NdtFrontEndProtocol {
    /**
     * Sends a NDT message
     *
     * @param message
     */
    void sendNdtMessage(String message);

    /**
     * Sends a NDT status message
     *
     * @param message
     */
    void sendNdtStatusMessage(String message);

    /**
     * Sends a control event, which signals change in the back-end state
     *
     * @param event
     * @return
     */
    boolean sendEvent(String event);
}
