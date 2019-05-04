package com.codethen.util.collections;

import java.util.function.Function;

public class MapTransformation implements Transformation {

    private Function f;

    public MapTransformation(Function f) {
        this.f = f;
    }

    public Function getFunction() {
        return f;
    }
}
