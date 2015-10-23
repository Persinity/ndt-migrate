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

package com.persinity.ndt.dbagent.relational;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashMap;
import java.util.Map;

import com.persinity.common.db.RelDb;

/**
 * Tracks agent deployment per NDT db.
 *
 * @author dyordanov
 */
public class DbAgentTracker {
    private Map<String, Map<Class<?>, Object>> dbToAgentDispatchedMap = new HashMap<>();

    /**
     * Use this method to signal the tracker that an agent has been dispatched on a given NDT DB.
     *
     * @param ndtDb
     * @param agent
     */
    public void agentDispatched(RelDb ndtDb, Object agent) {
        assertArgs(ndtDb, agent.getClass());
        final String userName = ndtDb.getUserName();
        Map<Class<?>, Object> ndtDispatchedAgents = dbToAgentDispatchedMap.get(userName);
        if (ndtDispatchedAgents == null) {
            ndtDispatchedAgents = new HashMap<>();
        }
        ndtDispatchedAgents.put(agent.getClass(), agent);
        dbToAgentDispatchedMap.put(userName, ndtDispatchedAgents);
    }

    /**
     * @param ndtDb
     * @param agentClazz
     * @return {@code true} if agent if the specified class has been tracked as dispatched for the given NDT DB.
     */
    public boolean isAgentDispatched(RelDb ndtDb, Class<?> agentClazz) {
        assertArgs(ndtDb, agentClazz);
        final String userName = ndtDb.getUserName();
        Map<Class<?>, Object> ndtDispatchedAgents = dbToAgentDispatchedMap.get(userName);
        if (ndtDispatchedAgents == null) {
            return false;
        }
        return ndtDispatchedAgents.containsKey(agentClazz);
    }

    /**
     * @param ndtDb
     * @param agentClazz
     * @param <T>
     * @return Agent of the given class if it has been dispatched on a given NDT DB, or {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T> T getDispatchedAgent(RelDb ndtDb, Class<?> agentClazz) {
        assertArgs(ndtDb, agentClazz);
        final String userName = ndtDb.getUserName();
        Map<Class<?>, Object> ndtDispatchedAgents = dbToAgentDispatchedMap.get(userName);
        if (ndtDispatchedAgents == null) {
            return null;
        }
        return (T) ndtDispatchedAgents.get(agentClazz);
    }

    private void assertArgs(final RelDb ndtDb, final Class<?> agentClazz) {
        notNull(ndtDb);
        notNull(agentClazz);
        assertArg(RelCdcAgent.class.isAssignableFrom(agentClazz) || RelClogAgent.class.isAssignableFrom(agentClazz)
                || RelSchemaAgent.class.isAssignableFrom(agentClazz));
    }

}
