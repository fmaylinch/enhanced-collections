package com.codethen.util.collections;

import java.util.Collection;
import java.util.Optional;

public class EnhancedIntCollectionImpl extends EnhancedCollectionImpl<Integer> implements EnhancedIntCollection {

    public EnhancedIntCollectionImpl(Collection<?> collection, boolean immutable, Collection<Transformation> ts) {
        super(collection, immutable, ts);
    }

    @Override
    public int sum() {
        return fold(0, Integer::sum);
    }

    @Override
    public Optional<Double> average() {

        final int[] sumAndCount = new int[2];

        forEach(x -> {
            sumAndCount[0] += x;
            sumAndCount[1] ++;
        });

        return sumAndCount[1] > 0 ?
                Optional.of((double)sumAndCount[0] / sumAndCount[1])
                : Optional.empty();
    }
}
