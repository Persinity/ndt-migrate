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
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.persinity.common.fp.RepeaterFunc;

/**
 * @author Doichin Yordanov
 */
public class RepeaterTupleFunc implements TupleFunc {

    private final RepeaterFunc<Iterator<Map<String, Object>>> repeater;

    public RepeaterTupleFunc() {
        repeater = new RepeaterFunc<Iterator<Map<String, Object>>>();
    }

    @Override
    public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
        return repeater.apply(input);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
