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

import static com.persinity.common.invariant.Invariant.assertArg;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorPath;
import com.persinity.common.Id;
import com.persinity.common.StringUtils;
import com.persinity.common.invariant.NotNull;
import com.persinity.common.logging.LogUtil;
import com.persinity.haka.IdleJob;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * Holds a {@link Job} and its state.
 *
 * @author Ivan Dachev
 */
public class JobState implements Serializable, Cloneable {

    public enum Status {
        NEW, PROCESSING, DONE
    }

    /**
     * @param job
     *         to create {@link JobState}
     * @param sessionId
     *         session {@link Id} that originally sent the {@link com.persinity.haka.impl.actor.message.NewMsg}
     * @param senderPath
     *         {@link ActorPath} to the new message sender
     */
    public JobState(final Job job, final Id sessionId, final ActorPath senderPath) {
        new NotNull("job").enforce(job);
        new NotNull("job.getJobProducerClass()").enforce(job.getJobProducerClass());

        this.job = job;
        this.sessionId = sessionId;
        this.senderPath = senderPath;

        setStatus(Status.NEW);

        children = new HashMap<>();
    }

    /**
     * @return {@link Job}
     */
    public Job getJob() {
        return job;
    }

    /**
     * Used to refresh the internal {@link Job} instance after calling to
     * {@link JobProducer#process} and {@link JobProducer#processed(Job, Job)}
     *
     * @param job
     *         {@link Job} to update
     */
    public void updateJob(final Job job) {
        assert job != null && job.getId() == this.job.getId();

        this.job = job;
    }

    /**
     * Set session {@link Id} that originally sent the {@link com.persinity.haka.impl.actor.message.NewMsg}
     * for which this {@link JobState} was created.
     *
     * @param sessionId
     *         to set
     */
    public void setSessionId(Id sessionId) {
        assert sessionId != null;

        this.sessionId = sessionId;
    }

    /**
     * @return session {@link Id}
     */
    public Id getSessionId() {
        return sessionId;
    }

    /**
     * @return senders {@link ActorPath}
     */
    public ActorPath getSenderPath() {
        return senderPath;
    }

    /**
     * @return current {@link Status}
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status
     *         {@link Status} to set
     */
    public void setStatus(final Status status) {
        assert status != null;

        this.status = status;
        statusUpdateTime = System.currentTimeMillis();
    }

    /**
     * @return last status update time in ms
     */
    public long getStatusUpdateTime() {
        return statusUpdateTime;
    }

    /**
     * @param all
     *         whether to reset status times to this and its children
     */
    public void resetStatusUpdateTime(final boolean all) {
        setStatus(getStatus());

        if (all) {
            for (final JobState child : children.values()) {
                child.resetStatusUpdateTime(true);
            }
        }
    }

    /**
     * @return true if all non-idle jobs are with status {@link Status#DONE}
     */
    public boolean areChildrenDone() {
        boolean allDone = true;
        for (final JobState childJobState : children.values()) {
            if (childJobState.getStatus() != JobState.Status.DONE && !(childJobState.getJob() instanceof IdleJob)) {
                allDone = false;
                break;
            }
        }
        return allDone;
    }

    /**
     * Remove all {@link IdleJob}.
     */
    public void cleanupIdleJobs() {
        final Set<Id> idsToRemove = new HashSet<>();
        for (final Id childJobStateId : children.keySet()) {
            final JobState childJobState = children.get(childJobStateId);
            if (childJobState.getJob() instanceof IdleJob) {
                idsToRemove.add(childJobStateId);
            }
        }
        for (final Id idToRemove : idsToRemove) {
            children.remove(idToRemove);
        }
    }

    /**
     * @return children map
     */
    public Map<Id, JobState> getChildren() {
        return children;
    }

    /**
     * @param children
     *         children {@link Job} to create {@link JobState} for and add to current one
     *         {@link IdleJob} are created in {@link Status#PROCESSING} all other in {@link Status#NEW}
     */
    public void appendChildren(final Set<Job> children) {
        for (final Job child : children) {
            final Id childId = child.getId().id;
            assertArg(!this.children.containsKey(childId), "Already have for parent: {} child: {}", this,
                    systemInfoString(child));
            assertArg(child.getId().parentId.equals(job.getId().id), "Parent ID mismatch for parent: {} child: {}",
                    this, systemInfoString(child));
            final JobState childJobState = new JobState(child, null, null);
            if (child instanceof IdleJob) {
                childJobState.setStatus(Status.PROCESSING);
            }
            this.children.put(childId, childJobState);
        }
    }

    /**
     * @param childJobState
     *         to remove
     */
    public void removeChild(JobState childJobState) {
        final Id childId = childJobState.getJob().getId().id;
        assertArg(this.children.containsKey(childId), "Not found for parent: {} child: {}", this, childJobState);
        this.children.remove(childId);
    }

    /**
     * @return {@link JobProducer} for current {@link Job}
     */
    public JobProducer getJobProducer() {
        if (jobProducer != null) {
            return jobProducer;
        }

        try {
            jobProducer = job.getJobProducerClass().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }

        return jobProducer;
    }

    @Override
    public JobState clone() {
        JobState res;
        try {
            Constructor<? extends JobState> constructor = this.getClass()
                    .getDeclaredConstructor(Job.class, Id.class, ActorPath.class);
            constructor.setAccessible(true);
            res = constructor.newInstance(job.clone(), sessionId, senderPath);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        res.status = status;
        res.statusUpdateTime = statusUpdateTime;
        for (final JobState child : children.values()) {
            final Id childId = child.getJob().getId().id;
            res.children.put(childId, child.clone());
        }
        return res;
    }

    @Override
    public String toString() {
        if (objToString == null) {
            objToString = LogUtil.formatPackageName(super.toString());
        }
        if (jobSystemInfo == null) {
            jobSystemInfo = systemInfoString(job);
        }
        return StringUtils.format("{}[{}][{}][children: {}][{}]", objToString, jobSystemInfo, status, children.size(),
                senderPath);
    }

    /**
     * @param job
     *         to create system info string
     * @return system info string for the {@link Job}
     */
    public static String systemInfoString(final Job job) {
        return StringUtils
                .format("{}-{}-{}{}", LogUtil.formatPackageName(job.getClass().getName()), job.getId().toShortString(),
                        LogUtil.formatPackageName(job.getJobProducerClass().getName()),
                        job instanceof IdleJob ? "-idle" : "");
    }

    private Job job;
    private Id sessionId;
    private final ActorPath senderPath;
    private Status status;
    private long statusUpdateTime;
    private final HashMap<Id, JobState> children;

    private transient String jobSystemInfo;
    private transient String objToString;
    private transient JobProducer jobProducer;

    private static final long serialVersionUID = -6624912568872966385L;
}
