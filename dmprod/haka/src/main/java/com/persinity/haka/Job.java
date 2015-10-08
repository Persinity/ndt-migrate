/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import java.io.Serializable;

/**
 * Represents a Job that can be handled by a {@link JobProducer}.
 *
 * @author Ivan Dachev
 */
public interface Job extends Serializable, Cloneable {
	/**
	 * @return {@link JobIdentity} for the Job.
	 */
	JobIdentity getId();

	/**
	 * @return The {@link JobProducer} implementation class that can handle the Job.
	 */
	Class<? extends JobProducer> getJobProducerClass();

	/**
	 * Should be implemented to properly clone the state of the Job.
	 *
	 * @return Clone of the Job
	 */
	Job clone();
}
