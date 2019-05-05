package com.codethen.util.collections;

import java.util.Optional;

public interface EnhancedIntCollection extends EnhancedCollection<Integer> {

    int sum();

    Optional<Double> average();
}
