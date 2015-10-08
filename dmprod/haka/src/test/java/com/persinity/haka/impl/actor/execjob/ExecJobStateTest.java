/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.execjob;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.RootActorPath;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.TestActorRefMock;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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