/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import java.util.Set;

/**
 * Interface to set the contract for producer of Jobs.
 * <p/>
 * The implementation should be made in that way that all Job's state is kept in the
 * {@link Job}. The states kept in the JobProducer should be used only to speedup
 * the process and to share resources between processing the jobs but not to keep a Job
 * specific state.
 * <p/>
 * In case of failure the JobProducer will be recreated and restored Jobs from snapshot
 * will start coming through process/processed methods. The implementation should handle
 * this and recreate any internal state only from these Jobs.
 *
 * @author Ivan Dachev
 */
public interface JobProducer<X extends Job, Y extends Job> {
    /**
     * Used to process a job.
     * <p/>
     * If a job is atomic then this method do it and return an empty set.
     * <p/>
     * If a job can be split in multiple jobs this method should return a set of children jobs.
     * <p/>
     * Once all of the children are done and the producer receives the {@link JobProducer#processed} calls then
     * it can return more children in the consequence calls to  {@link JobProducer#process}
     * <p/>
     * A composite job that returns only one child Job that extends {@link IdleJob} will be thread
     * as idle. Which means an idle time will be placed before next call to {@link JobProducer#process}.
     * Idle jobs will be not resend for processing and will be not send to {@link JobProducer#processed}.
     * <p/>
     * A composite job is considered as done if {@link JobProducer#process} method return an empty set indicating
     * no more children Jobs can be created.
     *
     * @param job
     *         to process
     * @return new set of children jobs
     */
    Set<Y> process(X job);

    /**
     * Should be called once a child Job is done.
     *
     * @param parentJob
     *         to handle child processed
     * @param childJob
     *         that was processed
     */
    void processed(X parentJob, Y childJob);
}
