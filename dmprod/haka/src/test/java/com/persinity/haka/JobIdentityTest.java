/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import static com.persinity.test.TestUtil.serDeser;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test for JobIdentity
 *
 * @author Ivan Dachev
 */
public class JobIdentityTest {

	@Test public void testConstruct() throws Exception {
		final JobIdentity parent = new JobIdentity();

		assertThat(parent.id, notNullValue());
		assertThat(parent.id, is(parent.parentId));

		final JobIdentity child = new JobIdentity(parent);
		assertThat(parent.id, is(child.parentId));
		assertThat(child.id, notNullValue());
		assertThat(parent.id, not(child.id));

		try {
			new JobIdentity(null);
			fail("Should throw NPE");
		} catch (final NullPointerException ex) {
			assertThat(ex.getMessage(), is("parent"));
		}
	}

	@SuppressWarnings({ "ObjectEqualsNull", "EqualsWithItself" })
	@Test public void testEquals() throws Exception {
		final JobIdentity parent = new JobIdentity();

		assertTrue(parent.equals(parent));

		assertFalse(parent.equals(null));
		assertFalse(parent.equals(new Object()));
		assertFalse(parent.equals(new JobIdentity()));

		final JobIdentity child1 = new JobIdentity(parent);

		assertTrue(child1.equals(child1));

		assertFalse(child1.equals(null));
		assertFalse(child1.equals(new Object()));
		assertFalse(child1.equals(new JobIdentity()));

		final JobIdentity child2 = new JobIdentity(parent);

		assertFalse(child1.equals(parent));
		assertFalse(parent.equals(child1));
		assertFalse(child1.equals(child2));
		assertFalse(child2.equals(child1));

		final JobIdentity subChild11 = new JobIdentity(child1);

		assertFalse(child1.equals(subChild11));
		assertFalse(subChild11.equals(child1));
		assertFalse(subChild11.equals(parent));
		assertFalse(parent.equals(subChild11));

		final JobIdentity child1New = serDeser(child1);
		assertTrue(child1 != child1New);
		assertTrue(child1New.equals(child1));
	}

	@Test public void testHashCode() throws Exception {
		final JobIdentity parent = new JobIdentity();

		assertTrue(parent.hashCode() != 0);

		final JobIdentity child1 = new JobIdentity(parent);
		assertThat(parent.hashCode(), not(child1.hashCode()));

		final JobIdentity child2 = new JobIdentity(parent);

		assertThat(parent.hashCode(), not(child2.hashCode()));
		assertThat(child1.hashCode(), not(child2.hashCode()));

		final JobIdentity child1New = serDeser(child1);
		assertThat(child1, not(sameInstance(child1New)));
		assertThat(child1.hashCode(), is(child1New.hashCode()));
	}

	@Test public void testToString() throws Exception {
		final JobIdentity parent = new JobIdentity();

		String str = parent.toString();
		assertThat(str, endsWith(String.format("(%s/%s)", parent.id, parent.id)));

		final JobIdentity child = new JobIdentity(parent);
		str = child.toString();
		assertThat(str, endsWith(String.format("(%s/%s)", child.parentId, child.id)));
	}

	@Test public void testToShortString() throws Exception {
		final JobIdentity parent = new JobIdentity();

		final String shortId = parent.id.toStringShort();

		String str = parent.toShortString();
		assertThat(str, is(String.format("%s/%s", shortId, shortId)));

		final JobIdentity child = new JobIdentity(parent);
		str = child.toShortString();
		assertThat(str,
				is(String.format("%s/%s", child.parentId.toStringShort(), child.id.toStringShort())));
	}

	@Test public void testCompareTo() throws Exception {
		final JobIdentity parent = new JobIdentity();

		assertThat(parent.compareTo(parent), is(0));

		final JobIdentity child = new JobIdentity(parent);

		assertThat(parent.compareTo(child), is(parent.id.compareTo(child.id)));
		assertThat(child.compareTo(parent), is(child.id.compareTo(parent.id)));

		assertThat(child.compareTo(parent)*-1, is(parent.compareTo(child)));

		assertThat(child.compareTo(null), is(-1));
	}

}