package com.codethen.util.collections;

import java.util.Collection;

public class Enhancer {

    /** Returns an immutable {@link EnhancedCollection} (see {@link #enhance(Collection, boolean)})*/
    public static <T> EnhancedCollection<T> enhance(Collection<T> collection) {
        return enhance(collection, true);
    }

    /**
     * Note that if you use non-immutable, you can't reuse middle transformations.
     *
     * If you use non-immutable:
     * c1 = enhance(asList(1, 2, 3))
     * c2 = c1.map(x => x+1) // c1 and c2 will be the same: (2, 3, 4)
     * c3 = c1.map(x => x*10) // c1, c2, c3 will be the same: (20, 30, 40)
     *
     * @param collection initial collection
     * @param immutable  true if you want to return a new object after every transformation
     */
    public static <T> EnhancedCollection<T> enhance(Collection<T> collection, boolean immutable) {
        return new EnhancedCollectionImpl(collection, immutable);
    }
}
