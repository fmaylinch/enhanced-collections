package com.codethen.util.collections;

import java.util.function.Predicate;

public class FilterTransformation implements Transformation {

    private Predicate p;

    public FilterTransformation(Predicate p) {
        this.p = p;
    }

    public Predicate getPredicate() {
        return p;
    }
}
