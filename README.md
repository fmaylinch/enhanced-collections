# Enhanced Collection

Experimenting with a kind of collection with
more methods like `map`, `filter`, `reduce`
but avoiding the verbosity of Java Streams.

Example:

```java
    EnhancedCollection<Integer> result =
        Enhancer.enhance(asList(2, 4, 5, 3, 1))
            .map(x -> x + 1)
            .filter(x -> x % 3 == 0)
            .map(x -> x + 1);

    assertThat(result, is(asList(4, 7)));
    assertThat(result.reduce(Integer::sum), is(Optional.of(11)));
```