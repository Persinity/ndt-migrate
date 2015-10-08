/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.persinity.common.StringUtils.extractLastSegments;

/**
 * Generates ID which base is a UUID and suffix segment incremental integer value.
 *
 * For example:
 * 986160bf-3229-49e1-afa0-1efba3b66175-1
 * 986160bf-3229-49e1-afa0-1efba3b66175-2
 * ...
 *
 * @author Ivan Dachev
 */
public class Id implements Serializable, Comparable<Id> {

	/**
	 * @return a new ID value.
	 */
	public static Id nextValue() {
		StringBuilder sb = new StringBuilder(BASE_ID.length() + 16);
		sb.append(BASE_ID).append(ID_SEGMENT_SEP).append(ID_COUNTER.incrementAndGet());
		return new Id(sb.toString());
	}

	private Id(final String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Id)) {
			return false;
		}

		Id other = (Id) obj;
		return id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(Id other) {
		if (other == null) {
			return -1;
		}
		return id.compareTo(other.id);
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * @return short string representation of id
	 */
	public String toStringShort() {
		return extractLastSegments(id, ID_SEGMENT_SEP, ID_LAST_SEGMENTS);
	}

	private final String id;

	private static final long serialVersionUID = 8812374042572113818L;
	private static final String BASE_ID = UUID.randomUUID().toString();
	private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);
	private static final int ID_LAST_SEGMENTS = 2;
	private static final char ID_SEGMENT_SEP = '-';
}
