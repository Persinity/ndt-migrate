/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

/**
 * A marker interface to notify that Job returned from {@link JobProducer#process(Job)}
 * should be considered idle and more jobs could appear on later calls.
 *
 * @author Ivan Dachev
 */
public interface IdleJob {

}
