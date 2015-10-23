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
