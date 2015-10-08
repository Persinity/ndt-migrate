/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.fp;

import com.google.common.base.Function;

/**
 * Higher order function.
 * 
 * @author Doichin Yordanov
 */
public interface Functor<F2, T2, F1, T1 extends Function<F2, T2>> extends Function<F1, T1> {
}
