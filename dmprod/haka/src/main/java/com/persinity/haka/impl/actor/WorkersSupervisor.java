/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.persinity.haka.impl.actor.pool.WorkerPool;
import com.persinity.haka.impl.actor.pool.WorkerPoolCluster;
import com.persinity.haka.impl.actor.pool.WorkerPoolLocal;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.FiniteDuration;

/**
 * Creates a pool and the workers. Makes supervising of the workers and set the fail policy.
 *
 * TODO set supervising policy
 *
 * @author Ivan Dachev
 */
public class WorkersSupervisor extends UntypedActor {
	protected WorkersSupervisor(String nodeId) {
		this.nodeId = nodeId;

		if (settings.isPoolImplCluster()) {
			pool = new WorkerPoolCluster(getContext().system(), settings.getPoolName());
		} else {
			pool = new WorkerPoolLocal(getContext().system(), settings.getPoolName());
		}

		createWorkers();
	}

	public static Props props(final String nodeId) {
		return Props.create(new Creator<WorkersSupervisor>() {
			private static final long serialVersionUID = -1625847653508801924L;

			@Override public WorkersSupervisor create() throws Exception {
				return new WorkersSupervisor(nodeId);
			}
		});
	}

	@Override public void postStop() throws Exception {
		super.postStop();

		pool.destroy();
	}

	@Override public void onReceive(Object msg) throws Exception {
		pool.tell(msg, getSender(), true);
	}

	public static void waitReady(ActorRef workersSupervisorRef, FiniteDuration timeout) {
		FiniteDuration checkDuration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Deadline deadline = timeout.fromNow();

		while (deadline.hasTimeLeft()) {
			try {
				Future<Object> actorRefFuture = Patterns
						.ask(workersSupervisorRef, Worker.PingPong.PING, Timeout.durationToTimeout(checkDuration));
				Worker.PingPong pingPong = (Worker.PingPong) Await
						.result(actorRefFuture, checkDuration.plus(checkDuration));
				if (pingPong == WorkerBase.PingPong.PONG) {
					return;
				} else {
					throw new RuntimeException(String.format("Unexpected replay: %s", pingPong));
				}
			} catch (Exception e) {
				if (!(e instanceof AskTimeoutException) && !(e instanceof TimeoutException)) {
					throw new RuntimeException(e);
				}
			}
		}

		throw new RuntimeException("Timeout on waiting for readiness");
	}

	protected void createWorkers() {
		for (int i = 0; i < settings.getWorkers(); i++) {
			String name = Worker.class.getSimpleName() + "-" + nodeId + "-" + i;
			ActorRef actor = getContext().actorOf(Worker.props(pool), name);
			log.debug(String.format("Created worker: %s", actor.path()));
		}
	}

	protected final String nodeId;

	protected final WorkerPool pool;

	protected final HakaSettings settings = HakaSettings.Provider.SettingsProvider
			.get(getContext().system());

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
}
