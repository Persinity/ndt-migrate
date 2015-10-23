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
package com.persinity.haka.impl.actor;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.persinity.common.invariant.Invariant;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Settings helper methods.
 *
 * @author Ivan Dachev
 */
public class SettingsHelper {
    public static final FiniteDuration ONE_SECOND = Duration.create(1, TimeUnit.SECONDS);

    public static void validateNotEmpty(final String name, final String value) {
        Invariant.assertArg(!value.isEmpty(), "Expected not empty: " + name);
    }

    public static void validateNotEmpty(final String name, final Collection<?> value) {
        Invariant.assertArg(!value.isEmpty(), "Expected not empty: " + name);
    }

    public static void validateIsPositive(final String name, final int value) {
        Invariant.assertArg(value > 0, "Expected positive: " + name + " = " + value);
    }

    public static void validateIsPositiveOrZero(final String name, final int value) {
        Invariant.assertArg(value >= 0, "Expected positive: " + name + " = " + value);
    }

    public static void validateGreaterThen(final String name, final FiniteDuration value, final FiniteDuration ref) {
        Invariant.assertArg(!value.lt(ref), "Expected " + name + " = " + value + " >= " + ref);
    }
}
