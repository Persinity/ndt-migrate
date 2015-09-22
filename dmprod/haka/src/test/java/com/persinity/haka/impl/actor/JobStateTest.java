/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorPath;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * @author Ivan Dachev
 */
public class JobStateTest {

    @Before
    public void setUp() {
        job = new TestJob(TestJobProducerOneChild.class);
        sessionId = Id.nextValue();
        actorPath = EasyMock.createNiceMock(ActorPath.class);
        jobState = new JobState(job, sessionId, actorPath);

        childJob = new TestJob(job, TestJobProducerOneChild.class);
    }

    @Test
    public void testGetJob() throws Exception {
        assertThat(jobState.getJob(), is((Job) job));
    }

    @Test
    public void testUpdateJob() throws Exception {
        assertThat(jobState.getJob(), is((Job) job));

        TestJob jobClone = (TestJob) job.clone();
        jobClone.state = "t";
        jobState.updateJob(jobClone);

        assertThat(jobState.getJob(), is((Job) jobClone));
    }

    @Test
    public void testSetSessionId() throws Exception {
        Id newSessionId = Id.nextValue();
        assertThat(jobState.getSessionId(), is(sessionId));
        jobState.setSessionId(newSessionId);
        assertThat(jobState.getSessionId(), is(newSessionId));
    }

    @Test
    public void testGetSessionId() throws Exception {
        assertThat(jobState.getSessionId(), is(sessionId));
    }

    @Test
    public void testGetSenderPath() throws Exception {
        assertThat(jobState.getSenderPath(), is(actorPath));
    }

    @Test
    public void testGetStatus() throws Exception {
        assertThat(jobState.getStatus(), is(JobState.Status.NEW));
    }

    @Test
    public void testSetStatus() throws Exception {
        assertThat(jobState.getStatus(), is(JobState.Status.NEW));
        jobState.setStatus(JobState.Status.DONE);
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));
    }

    @Test
    public void testGetStatusUpdateTime() throws Exception {
        long updateTime = jobState.getStatusUpdateTime();
        assertThat(updateTime, not(0L));
    }

    @Test
    public void testResetStatusUpdateTime() throws Exception {
        long updateTime = jobState.getStatusUpdateTime();
        Thread.sleep(1);
        jobState.resetStatusUpdateTime(false);
        long updateTimeUpdated = jobState.getStatusUpdateTime();
        assertThat(updateTime, not(updateTimeUpdated));

        jobState.appendChildren(Collections.<Job>singleton(childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);

        updateTime = childJobState.getStatusUpdateTime();
        jobState.resetStatusUpdateTime(false);
        updateTimeUpdated = childJobState.getStatusUpdateTime();
        assertThat(updateTime, is(updateTimeUpdated));

        updateTime = childJobState.getStatusUpdateTime();
        Thread.sleep(1);
        jobState.resetStatusUpdateTime(true);
        updateTimeUpdated = childJobState.getStatusUpdateTime();
        assertThat(updateTime, not(updateTimeUpdated));
    }

    @Test
    public void testIsChildrenDone() throws Exception {
        assertTrue(jobState.areChildrenDone());

        jobState.appendChildren(Collections.<Job>singleton(childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);

        assertFalse(jobState.areChildrenDone());

        childJobState.setStatus(JobState.Status.DONE);

        assertTrue(jobState.areChildrenDone());
    }

    @Test
    public void testGetChildren() throws Exception {
        jobState.appendChildren(Collections.<Job>singleton(childJob));
        Map<Id, JobState> children = jobState.getChildren();
        assertThat(children, notNullValue());
        assertThat(children.size(), is(1));
        assertThat(children.get(childJob.getId().id).getJob(), CoreMatchers.<Job>is(childJob));
    }

    @Test
    public void testAppendChildren() throws Exception {
        assertThat(jobState.getChildren().size(), is(0));
        jobState.appendChildren(Collections.<Job>singleton(childJob));
        assertThat(jobState.getChildren().get(childJob.getId().id).getJob(), CoreMatchers.<Job>is(childJob));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendChildren_duplicateFail() throws Exception {
        assertThat(jobState.getChildren().size(), is(0));
        jobState.appendChildren(Collections.<Job>singleton(childJob));
        jobState.appendChildren(Collections.<Job>singleton(childJob));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendChildren_invalidParentId() throws Exception {
        assertThat(jobState.getChildren().size(), is(0));
        jobState.appendChildren(Collections.<Job>singleton(new TestJob(TestJobProducerOneChild.class)));
    }

    @Test
    public void testRemoveChildren() throws Exception {
        jobState.appendChildren(Collections.<Job>singleton(childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        jobState.removeChild(childJobState);
        assertThat(jobState.getChildren().size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveChildren_Unknown() throws Exception {
        JobState childJobState = new JobState(childJob, Id.nextValue(), actorPath);
        jobState.removeChild(childJobState);
    }

    @Test
    public void testGetJobProducer() throws Exception {
        JobProducer producer = jobState.getJobProducer();
        assertThat(producer, instanceOf(TestJobProducerOneChild.class));
        JobProducer producerSecond = jobState.getJobProducer();
        assertThat(producerSecond, is(producer));
    }

    @Test(expected = RuntimeException.class)
    public void testGetJobProducer_NewInstanceFail() throws Exception {
        JobState jobStateFail = new JobState(new TestJob(TestJobProducerThrowOnConstruct.class), Id.nextValue(),
                actorPath);
        jobStateFail.getJobProducer();
    }

    @Test
    public void testClone() throws Exception {
        jobState.appendChildren(Collections.<Job>singleton(childJob));
        childJob.state = "t";
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);

        JobState jobStateClone = jobState.clone();
        JobState childJobStateClone = jobStateClone.getChildren().get(childJob.getId().id);

        assertThat(jobStateClone.getStatus(), is(jobState.getStatus()));
        assertThat(jobStateClone.getJob().getId(), is(job.getId()));
        assertThat(((TestJob) jobStateClone.getJob()).state, is(job.state));

        assertThat(childJobStateClone.getStatus(), is(childJobState.getStatus()));
        assertThat(childJobStateClone.getJob().getId(), is(childJob.getId()));
        assertThat(((TestJob) childJobStateClone.getJob()).state, is(childJob.state));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(jobState.toString(), not(""));
        assertThat(jobState.toString(), containsString(JobState.systemInfoString(job)));
    }

    @Test
    public void testSystemInfoString() throws Exception {
        assertThat(JobState.systemInfoString(job), not(""));
    }

    private TestJob job;
    private Id sessionId;
    private ActorPath actorPath;
    private JobState jobState;

    private TestJob childJob;
}
