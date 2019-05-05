package com.codethen.util.collections;

import org.junit.Test;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Optional;

import static com.codethen.util.collections.Enhancer.immutable;
import static com.codethen.util.collections.Enhancer.mutable;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnhancerTest {

    @Test
    public void mapAndFilterImmutable() {

        final EnhancedCollection<Integer> list = immutable(asList(2, 4, 5, 3, 1));

        Collection<Integer> result = list
                .map(x -> x + 1)
                .filter(x -> x % 3 == 0)
                .map(x -> x + 1);

        assertThat(result, is(asList(4, 7)));
        assertThat(list, is(asList(2, 4, 5, 3, 1))); // original list not modified
    }

    @Test
    public void mapAndFilterMutable() {

        final EnhancedCollection<Integer> list = mutable(asList(2, 4, 5, 3, 1));

        Collection<Integer> result = list
                .map(x -> x + 1)
                .filter(x -> x % 3 == 0)
                .map(x -> x + 1);

        assertThat(result, is(asList(4, 7)));
        assertThat(list == result, is(true)); // same object is reused
    }

    @Test
    public void reuseMiddleResult() {

        final EnhancedCollection<Integer> list = immutable(asList(2, 4, 5, 3, 1, 2));

        EnhancedCollection<Integer> middleResult = list
                .map(x -> x + 1)
                .filter(x -> x % 3 == 0);

        assertThat(middleResult.map(x -> x + 1), is(asList(4, 7, 4)));
        assertThat(middleResult.map(x -> x + 2), is(asList(5, 8, 5)));
        assertThat(middleResult.filter(x -> x != 6), is(asList(3, 3)));
    }

    @Test
    public void mutableEnhancedCollection() {

        final EnhancedCollection<Integer> list = mutable(asList(2, 4, 5, 3, 1, 2));

        list.map(x -> x + 1).filter(x -> x % 3 == 0); // list gets changed

        assertThat(list, is(asList(3, 6, 3)));
        list.map(x -> x + 1); // This is mutating list, leaving (4, 7, 4)
        assertThat(list.filter(x -> x != 7), is(asList(4, 4)));
    }

    @Test
    public void anyOrAllMatch() {

        EnhancedCollection<Integer> list = immutable(asList(2, 4, 5, 3, 1, 2));

        assertThat(list.anyMatch(x -> x == 5), is(true));
        assertThat(list.anyMatch(x -> x == 6), is(false));
        assertThat(list.allMatch(x -> x < 10), is(true));
        assertThat(list.allMatch(x -> x > 1), is(false));

        // 3, 5, 3
        EnhancedCollection<Integer> list2 = list.map(x -> x + 1).filter(x -> x % 2 == 1);

        assertThat(list2.anyMatch(x -> x == 5), is(true));
        assertThat(list2.anyMatch(x -> x == 1), is(false));
        assertThat(list2.allMatch(x -> x >= 3), is(true));
        assertThat(list2.allMatch(x -> x == 3), is(false));
    }

    @Test
    public void first() {

        EnhancedCollection<String> list = immutable(asList("dog", "cat", "horse", "ant", "crocodile"));

        assertThat(list.filter(w -> w.length() > 3).first(), is(Optional.of("horse")));
        assertThat(list.filter(w -> w.length() < 3).first(), is(Optional.empty()));
        assertThat(list.first(w -> w.length() > 5), is(Optional.of("crocodile")));
        assertThat(list.first(w -> w.length() < 3), is(Optional.empty()));
    }
}