/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka.context;

/**
 * TODO move to haka
 *
 * @author Ivan Dachev
 */
public class ContextFactoryProvider {

    /**
     * @return EtlContextFactory
     */
    public static ContextFactory getFactory() {
        return factory;
    }

    //	TODO initialize it with dependency injection framework like spring.
    //	@Autowired(required = true)
    //	private EtlContextFactoryProvider(EtlContextFactory _factory) {
    //		factory = _factory;
    //	}

    private static ContextFactory factory = new LocalContextFactory();
}
