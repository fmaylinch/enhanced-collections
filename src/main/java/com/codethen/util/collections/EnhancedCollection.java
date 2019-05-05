package com.codethen.util.collections;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface EnhancedCollection<T> extends Collection<T> {

    <R> EnhancedCollection<R> map(Function<T, R> f);

    EnhancedCollection<T> filter(Predicate<T> p);

    Optional<T> first();

    Optional<T> first(Predicate<T> p);

    boolean anyMatch(Predicate<T> p);

    boolean allMatch(Predicate<T> p);

    Collection<T> get();
}
