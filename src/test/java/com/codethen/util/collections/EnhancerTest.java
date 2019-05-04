package com.codethen.util.collections;

import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnhancerTest {

    @Test
    public void simpleTest() {

        final EnhancedCollection<Integer> list = Enhancer.enhance(asList(2, 4, 5, 3, 1));

        Collection<Integer> result = list
                .map(x -> x + 1)
                .filter(x -> x % 3 == 0)
                .map(x -> x + 1);

        assertThat(result, is(asList(4, 7)));
        assertThat(list, is(asList(2, 4, 5, 3, 1))); // original list not modified
    }

    @Test
    public void testReuseMiddleResult() {

        final EnhancedCollection<Integer> list = Enhancer.enhance(asList(2, 4, 5, 3, 1, 2));

        EnhancedCollection<Integer> middleResult = list
                .map(x -> x + 1)
                .filter(x -> x % 3 == 0);

        assertThat(middleResult.map(x -> x + 1), is(asList(4, 7, 4)));
        assertThat(middleResult.map(x -> x + 2), is(asList(5, 8, 5)));
        assertThat(middleResult.filter(x -> x != 6), is(asList(3, 3)));
    }

    @Test
    public void testMutableEnhancedCollection() {

        final EnhancedCollection<Integer> list = Enhancer.enhance(asList(2, 4, 5, 3, 1, 2), false);

        list.map(x -> x + 1).filter(x -> x % 3 == 0); // list gets changed

        assertThat(list, is(asList(3, 6, 3)));
        list.map(x -> x + 1); // This is mutating list, leaving (4, 7, 4)
        assertThat(list.filter(x -> x != 7), is(asList(4, 4)));
    }
}