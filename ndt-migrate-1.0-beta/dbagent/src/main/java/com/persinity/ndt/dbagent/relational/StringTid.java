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
package com.persinity.ndt.dbagent.relational;

import com.persinity.common.invariant.NotEmpty;
import com.persinity.ndt.db.TransactionId;

/**
 * @author Doichin Yordanov
 */
public class StringTid implements TransactionId {

    private final String tidValue;

    public StringTid(final String tidValue) {
        new NotEmpty("tidValue").enforce(tidValue);
        this.tidValue = tidValue;
    }

    @Override
    public boolean equals(final Object arg0) {
        if (this == arg0) {
            return true;
        }
        if (!(arg0 instanceof StringTid)) {
            return false;
        }
        final StringTid that = (StringTid) arg0;
        return tidValue.equals(that.tidValue);
    }

    @Override
    public int hashCode() {
        return tidValue.hashCode();
    }

    @Override
    public String toString() {
        return tidValue;
    }

}
