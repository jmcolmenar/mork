package es.urjc.etsii.grafo.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Fast integer set implementation compatible with Java Collections API.
 * Should be used when int range is known and not extremely big.
 * Collection is always backed by an static array.
 * TODO Bitmasks
 */
public class IntSet extends AbstractSet<Integer> {

    private int size;
    private boolean[] data;

    /**
     * Create a new IntSet that can hold values between [0, maxValue-1]
     * @param maxValue
     */
    public IntSet(int maxValue) {
        if (maxValue <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }

        this.data = new boolean[maxValue];
    }

    /**
     * Copy an Intset
     */
    public IntSet(IntSet set) {
        this.data = Arrays.copyOf(set.data, set.data.length);
        this.size = set.size;
    }

    public IntSet(Set<Integer> set){
        this((IntSet) set); // TODO assumes it is an intset, should be able to accept HashSets
    }

    @Override
    public boolean add(Integer integer) {
        return this.fastAdd(integer);
    }

    /**
     * No casting overhead.
     * @return
     */
    public boolean fastAdd(int n){
        invariant(n);

        // Collection already contains element, return false (not modified)
        if(this.data[n]){
            return false;
        }

        // Collection did not contain element, return true (modified)
        data[n] = true;
        size++;
        return true;
    }

    private void invariant(int n) {
        if(n >= this.data.length){
            throw new IllegalArgumentException(String.format("Index out of bounds, IntSize %s, argument %s", this.data.length, n));
        }

        if(n < 0){
            throw new IllegalArgumentException(String.format("Negative ints are not allowed: n=%s", n));
        }
    }


    @Override
    public boolean remove(Object o) {
        if(!(o instanceof Integer)){
            throw new IllegalArgumentException(String.format("Invalid object type (%s), collection can only store ints", o.getClass().getSimpleName()));
        }
        return fastRemove((Integer) o);
    }

    /**
     * Same as remove(), but without casting and object validation overhead
     * @return
     */
    public boolean fastRemove(int n){
        invariant(n);

        if(!this.data[n]){
            return false; // Collection not modified
        }

        this.data[n] = false;
        size--;
        return true; // Collection modified
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntSetIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    private class IntSetIterator implements Iterator<Integer>{

        private boolean currentUsed = true;
        private int nextValid = -1;

        @Override
        public boolean hasNext() {
            if(!currentUsed){
                return true;
            }

            boolean validNext = false;
            while(++nextValid < data.length){
                if (data[nextValid]) {
                    validNext = true;
                    currentUsed = false;
                    break;
                }
            }
            return validNext;
        }

        @Override
        public Integer next() {
            if(currentUsed){
                throw new IllegalStateException("Called next() without calling hashNext() first");
            }
            currentUsed = true;
            return nextValid;
        }

        @Override
        public void remove() {
            fastRemove(nextValid);
        }
    }
}
