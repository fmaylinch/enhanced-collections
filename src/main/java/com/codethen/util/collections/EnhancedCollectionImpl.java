package com.codethen.util.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnhancedCollectionImpl<T> extends AbstractCollection<T> implements EnhancedCollection<T> {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedCollectionImpl.class);

    private Collection<?> collection;
    private Collection<T> calculated;
    private boolean immutable;
    private Collection<Transformation> ts;

    /**
     * @param collection  original collection to enhance
     * @param immutable   true if you want this object to be immutable (transformations return new objects)
     */
    public EnhancedCollectionImpl(Collection<?> collection, boolean immutable) {
        this(collection, immutable, immutable ? Collections.emptyList() : new ArrayList<>());
    }

    private EnhancedCollectionImpl(Collection<?> collection, boolean immutable, Collection<Transformation> ts) {
        this.collection = collection;
        this.immutable = immutable;
        this.ts = ts;
    }

    @Override
    public <R> EnhancedCollection<R> map(Function<T, R> f) {
        return getEnhancedCollectionAfterApplying(new MapTransformation(f));
    }

    @Override
    public EnhancedCollection<T> filter(Predicate<T> p) {
        return getEnhancedCollectionAfterApplying(new FilterTransformation(p));
    }

    private <R> EnhancedCollection<R> getEnhancedCollectionAfterApplying(Transformation t) {

        if (immutable) {
            return new EnhancedCollectionImpl<>(collection, true, append(ts, t));
        } else {
            this.ts.add(t);
            this.calculated = null;
            return (EnhancedCollection<R>) this;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return get().iterator();
    }

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean equals(Object obj) {
        return get().equals(obj);
    }

    @Override
    public Collection<T> get() {

        if (calculated != null) {
            return calculated;
        }

        if (ts == null || ts.isEmpty()) {
            return (Collection<T>) collection;
        }

        logger.info("Producing collection with " + ts.size() + " transformations");

        final Collection<T> result = new ArrayList<>();

        for (Object x : collection) {

            boolean include = true;

            for (Transformation t : ts) {

                if (t instanceof MapTransformation) {
                    Function map = ((MapTransformation) t).getFunction();
                    x = map.apply(x);

                } else if (t instanceof FilterTransformation) {

                    Predicate predicate = ((FilterTransformation) t).getPredicate();
                    if (!predicate.test(x)) {
                        include = false;
                        break;
                    }
                } else {
                    throw new ClassCastException("Unexpected transformation type: " + t.getClass());
                }
            }

            if (include) {
                result.add((T) x);
            }
        }

        if (immutable) {
            this.calculated = result;
        } else {
            this.collection = result;
            this.ts = new ArrayList<>();
        }

        return result;
    }

    /**
     * TODO: Any better way to append new element to immutable collection?
     * We could just make an ad-hoc implementation of a linked collection with a functional append(x)
     */
    private <V> Collection<V> append(Collection<V> fs, V f) {
        final List<V> newFs = new ArrayList<>(fs);
        newFs.add(f);
        return newFs;
    }
}
