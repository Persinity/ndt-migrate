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
package com.persinity.common.invariant;

import java.util.Collection;

import com.persinity.common.collection.Tree;

/**
 * Not null and not empty {@code String} invariant
 *
 * @author Doichin Yordanov
 */
public class NotEmpty extends Invariant {

    public NotEmpty(final String... argName) {
        super(argName);
    }

    public void enforce(final String... arg) throws RuntimeException {
        for (int i = 0; i < arg.length; i++) {
            new NotNull(getArgName()[i]).enforce(arg[i]);
            Invariant.assertArg(!arg[i].trim().isEmpty(), getArgName()[i] + " is empty!");
        }
    }

    public void enforce(final Collection<?>... arg) throws RuntimeException {
        for (int i = 0; i < arg.length; i++) {
            new NotNull(getArgName()[i]).enforce(arg[i]);
            Invariant.assertArg(!arg[i].isEmpty(), getArgName()[i] + " is empty!");
        }
    }

    public void enforce(final Tree<?>... arg) throws RuntimeException {
        for (int i = 0; i < arg.length; i++) {
            new NotNull(getArgName()[i]).enforce(arg[i]);
            Invariant.assertArg(arg[i].getRoot() != null, getArgName()[i] + " is empty!");
        }
    }

}
