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
package com.persinity.common.db.metainfo;

import com.persinity.common.collection.WeightedDirectedEdge;
import com.persinity.common.db.metainfo.constraint.FK;

/**
 * @author Doichin Yordanov
 */
public class FKEdge extends WeightedDirectedEdge<String, FK, String> {

    public FKEdge(final String src, final FK fk, final String dst) {
        super(src, fk, dst);
    }

}
