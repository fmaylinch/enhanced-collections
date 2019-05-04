package com.codethen.util.collections;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public interface EnhancedCollection<T> extends Collection<T> {

    <R> EnhancedCollection<R> map(Function<T, R> f);

    EnhancedCollection<T> filter(Predicate<T> p);

    Collection<T> get();
}
