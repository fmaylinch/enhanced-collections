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

    @Override
    public Optional<T> first() {

        final Iterator<T> it = iterator();
        return it.hasNext() ? Optional.of(it.next()) : Optional.empty();
    }

    @Override
    public Optional<T> first(Predicate<T> p) {

        final Iterator<T> it = this.filter(p).iterator();
        return it.hasNext() ? Optional.of(it.next()) : Optional.empty();
    }

    @Override
    public boolean anyMatch(Predicate<T> p) {

        return findMatch(p, true, true);
    }

    @Override
    public boolean allMatch(Predicate<T> p) {

        return findMatch(p, false, false);
    }

    @Override
    public Iterator<T> iterator() {

        if (calculated != null) {
            return calculated.iterator();
        }

        logger.info("Producing iterator with " + ts.size() + " transformations");

        return new Iterator<T>() {

            final Iterator<?> it = collection.iterator();

            T next = getNext();
            boolean hasNext;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                T currentNext = next;
                next = getNext();
                return currentNext;
            }

            private T getNext() {

                while(it.hasNext()) {

                    Object x = it.next();
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
                        hasNext = true;
                        return (T) x;
                    }
                }

                hasNext = false;
                return null;
            }
        };
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

        logger.info("Producing collection");
        final Collection<T> result = new ArrayList<>();

        final Iterator<T> iterator = iterator();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        if (immutable) {
            this.calculated = result;
        } else {
            this.collection = result;
            this.ts = new ArrayList<>();
        }

        return result;
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

    private boolean findMatch(Predicate<T> p, boolean value, boolean result) {

        for (T t : this)
            if (p.test(t) == value)
                return result;

        return !result;
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
