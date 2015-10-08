/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.common.invariant.Invariant;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

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
