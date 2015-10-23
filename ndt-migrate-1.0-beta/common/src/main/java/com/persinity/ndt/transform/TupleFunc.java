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

import com.google.common.base.Function;

/**
 * Function that transforms set of tuples to a set of tuples.<BR>
 * A tuple is associative array (map) of objects. It can represent a table row or entity instance data.
 *
 * @author Doichin Yordanov
 */
public interface TupleFunc extends Function<Iterator<Map<String, Object>>, Iterator<Map<String, Object>>> {

}
