/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import java.util.Objects;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.persinity.common.StringUtils;
import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.common.invariant.Invariant;

/**
 * Graph related information about a vertex.<BR>
 * 
 * @author Doichin Yordanov
 */
public class VertexInfo<V, E extends ComparableDirectedEdge<V, Integer, V>> {
	private final V v;
	private int degreeBalance;
	private int weightBalance;
	private int degree;
	private Integer hashCode;
	private String toString;

	public static <V, E extends ComparableDirectedEdge<V, Integer, V>> VertexInfo<V, E> of(final DirectedGraph<V, E> dg,
			final V v) {
		Invariant.assertArg(dg != null);
		return new VertexInfo<V, E>(v, dg.edgesOf(v));
	}

	VertexInfo(final V v, final Set<E> edges) {
		Invariant.assertArg(v != null);

		this.v = v;
		if (edges != null) {
			for (final E e : edges) {
				handleEdgeAdded(v, e);
			}
		}
	}

	public V vertex() {
		return v;
	}

	/**
	 * @return The number of incoming - number of outgoing edges
	 */
	public int degreeBalance() {
		return degreeBalance;
	}

	/**
	 * In case the edge has no weight, this method behaves as {@link VertexInfo#degreeBalance()}
	 * 
	 * @return The sum of weight of incoming - the weight of outgoing edges
	 */
	public int weightBalance() {
		return weightBalance;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof VertexInfo)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final VertexInfo<V, ComparableDirectedEdge<V, Integer, V>> that = (VertexInfo<V, ComparableDirectedEdge<V, Integer, V>>) obj;
		final boolean result = this.vertex().equals(that.vertex()) && this.degree() == that.degree()
				&& this.weightBalance() == that.weightBalance() && this.degreeBalance() == that.degreeBalance();
		return result;
	}

	public int degree() {
		return degree;
	}

	@Override
	public int hashCode() {
		if (hashCode == null) {
			hashCode = Objects.hash(vertex(), degree(), weightBalance(), degreeBalance());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		if (toString == null) {
			toString = StringUtils.format("{}(degree = {}, edgeDegreeBalance = {}, edgeWeightBalance = {})", vertex(),
					degree(), degreeBalance(), weightBalance());
		}
		return toString;
	}

	private void handleEdgeAdded(final V v, final E e) {
		final boolean edgeIsOutgoing = v.equals(e.src());
		degreeBalance += edgeIsOutgoing ? -1 : 1;
		weightBalance += edgeIsOutgoing ? -e.weight() : e.weight();
		degree++;
	}

}
