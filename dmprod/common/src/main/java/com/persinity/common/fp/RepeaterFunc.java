/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.fp;

import com.google.common.base.Function;

/**
 * {@link Function} that repeats the passed input.
 * 
 * @author Doichin Yordanov
 */
public class RepeaterFunc<T> implements Function<T, T> {

    @Override
    public T apply(final T input) {
        return input;
    }

}
