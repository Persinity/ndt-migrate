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
package com.persinity.common.invariant;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.Tree;

/**
 * @author Doichin Yordanov
 */
public class NotEmptyTest {

    @Test(expected = NullPointerException.class)
    public void testEnforceNullString() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final String aString = null, bString = null;
        ne.enforce(aString, bString);
    }

    @Test(expected = NullPointerException.class)
    public void testEnforceNullCollection() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final Collection<String> aCollection = null, bCollection = null;
        ne.enforce(aCollection, bCollection);
    }

    @Test(expected = NullPointerException.class)
    public void testEnforceNullTree() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final Tree<Integer> aTree = null, bTree = null;
        ne.enforce(aTree, bTree);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnforceEmptyList() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final List<String> aCollection = Collections.emptyList();
        final List<String> bCollection = Collections.emptyList();
        ne.enforce(aCollection, bCollection);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnforceEmptyTree() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final List<String> aCollection = Collections.emptyList();
        final List<String> bCollection = Collections.emptyList();
        final Tree<String> aTree = CollectionUtils.newTree(aCollection);
        final Tree<String> bTree = CollectionUtils.newTree(bCollection);
        ne.enforce(aTree, bTree);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnforceEmptyString() {
        final NotEmpty ne = new NotEmpty("a", "b");

        final String aString = "", bString = "";
        ne.enforce(aString, bString);
    }

    @Test
    public void testEnforce() {
        final NotEmpty ne = new NotEmpty("a", "b");

        ne.enforce("astring", "bstring");
        ne.enforce(Collections.singleton("astring"), Collections.singletonList("bstring"));
        ne.enforce(CollectionUtils.newTree(Collections.singletonList("astring")),
                CollectionUtils.newTree(Collections.singletonList("bstring")));
    }

}
