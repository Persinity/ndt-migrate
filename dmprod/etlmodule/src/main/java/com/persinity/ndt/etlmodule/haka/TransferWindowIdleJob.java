/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka;

import static com.persinity.common.invariant.Invariant.assertArg;

import com.persinity.common.db.Closeable;
import com.persinity.haka.IdleJob;
import com.persinity.haka.JobIdentity;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Idle window job used when is empty.
 *
 * @author Ivan Dachev
 */
public class TransferWindowIdleJob<S extends Closeable, D extends Closeable> extends TransferWindowJob<S, D>
        implements IdleJob {
    public TransferWindowIdleJob(final JobIdentity id, final TransferWindow transferWindow) {
        super(id, transferWindow);
        assertArg(transferWindow.isEmpty());
    }
}
