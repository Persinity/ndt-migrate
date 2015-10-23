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
package com.persinity.haka.impl.actor.execjob;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.RootActorPath;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.TestActorRefMock;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;

/**
 * @author Ivan Dachev
 */
public class ExecJobStateTest {
    @Test
    public void testClone() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        Address address = new Address("akka", "haka");
        final RootActorPath path = new RootActorPath(address, "path");

        ActorRef sender = new TestActorRefMock(path);

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);

        ExecJobState cloned = jobState.clone();

        assertTrue(jobState != cloned);

        assertThat(jobState.getSender(), is(sender));
        assertThat(jobState.getJob().getId(), is(job.getId()));
    }
}