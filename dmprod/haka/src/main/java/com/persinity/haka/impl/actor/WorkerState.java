/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.persinity.common.Id;
import com.persinity.common.logging.LogUtil;

/**
 * The state class is holding the {@link JobState} on which worker works on.
 *
 * @author Ivan Dachev
 */
public class WorkerState implements Serializable, Cloneable {

    private static final long serialVersionUID = -9191894188542696848L;

    public WorkerState() {
        jobStates = new HashMap<>();
    }

    @Override
    public WorkerState clone() {
        WorkerState state = new WorkerState();
        for (JobState jobState : jobStates.values()) {
            state.addJobState(jobState.clone());
        }
        return state;
    }

    public JobState getJobState(Id jobId) {
        return jobStates.get(jobId);
    }

    public HashMap<Id, JobState> getJobStates() {
        return jobStates;
    }

    public void addJobState(JobState jobState) {
        if (jobStates.containsKey(jobState.getJob().getId().id)) {
            throw new IllegalStateException(String.format("We already processing %s", jobState));
        }

        this.jobStates.put(jobState.getJob().getId().id, jobState);
    }

    public void removeJobState(Id jobId) {
        jobStates.remove(jobId);
    }

    public String dumpJobStatesIds() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Id> keys = new ArrayList<>(jobStates.keySet());
        Collections.sort(keys);
        sb.append(keys.size());
        for (Id jobId : keys) {
            sb.append(",");
            JobState jobState = jobStates.get(jobId);
            sb.append(jobState.getStatus().toString().charAt(0));
            sb.append("-job-");
            sb.append(jobState.getJob().getId().toShortString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", LogUtil.formatPackageName(super.toString()), dumpJobStatesIds());
    }

    private final HashMap<Id, JobState> jobStates;
}
