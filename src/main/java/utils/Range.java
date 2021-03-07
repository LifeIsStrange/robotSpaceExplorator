package utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

// emulate integer ranges in java
// code borrowed from https://stackoverflow.com/a/16570509
// it is just a syntactic sugar for classical for loops
public class Range implements Iterable<Integer> {

    private int limit;

    public Range(int limit) {
        this.limit = limit;
    }

    @Override
    public Iterator<Integer> iterator() {
        final int max = limit;
        return new Iterator<Integer>() {

            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < max;
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    return current++;
                } else {
                    throw new NoSuchElementException("Range reached the end");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Can't remove values from a Range");
            }
        };
    }

    public static Range range(int max) {
        return new Range(max);
    }
}
