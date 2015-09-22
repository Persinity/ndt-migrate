/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import com.persinity.haka.IdleJob;

/**
 * {@link IdleJob} handler interface.
 *
 * @author Ivan Dachev
 */
public interface IdleJobHandler {
    void handleIdleJob() throws Exception;
}
