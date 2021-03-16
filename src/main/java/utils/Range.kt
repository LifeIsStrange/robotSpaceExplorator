package utils

// emulate integer ranges in java
// code borrowed from https://stackoverflow.com/a/16570509
// it is just a syntactic sugar for classical for loops
class Range(private val limit: Int) : Iterable<Int?> {
    override fun iterator(): MutableIterator<Int?> {
        val max = limit
        return object : MutableIterator<Int?> {
            private var current = 0
            override fun hasNext(): Boolean {
                return current < max
            }

            override fun next(): Int {
                return if (hasNext()) {
                    current++
                } else {
                    throw NoSuchElementException("Range reached the end")
                }
            }

            override fun remove() {
                throw UnsupportedOperationException("Can't remove values from a Range")
            }
        }
    }

    companion object {
        fun range(max: Int): Range {
            return Range(max)
        }
    }
}