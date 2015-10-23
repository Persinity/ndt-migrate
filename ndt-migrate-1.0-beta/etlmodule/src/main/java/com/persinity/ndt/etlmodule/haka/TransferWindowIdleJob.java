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
