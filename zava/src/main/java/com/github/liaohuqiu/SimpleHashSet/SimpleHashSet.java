package com.github.liaohuqiu.SimpleHashSet;

import java.util.*;

public class SimpleHashSet<T> extends AbstractSet<T> implements Set<T>, Cloneable {

    private static final int MINIMUM_CAPACITY = 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final SimpleHashSetEntry[] EMPTY_TABLE = new SimpleHashSetEntry[MINIMUM_CAPACITY >>> 1];
    transient SimpleHashSetEntry<T>[] mTable;
    transient int mSize;
    private transient int threshold;
    private SimpleHashSetEntry<T> mEntryForNull;

    public SimpleHashSet() {
        mTable = EMPTY_TABLE;
        // Forces first put invocation to replace EMPTY_TABLE
        threshold = -1;
    }

    public SimpleHashSet(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity: " + capacity);
        }

        if (capacity == 0) {
            SimpleHashSetEntry<T>[] tab = EMPTY_TABLE;
            mTable = tab;
            threshold = -1; // Forces first put() to replace EMPTY_TABLE
            return;
        }

        if (capacity < MINIMUM_CAPACITY) {
            capacity = MINIMUM_CAPACITY;
        } else if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        } else {
            capacity = roundUpToPowerOfTwo(capacity);
        }
        makeTable(capacity);
    }

    public SimpleHashSet(Collection<? extends T> collection) {
        this(collection.size() < 6 ? 11 : collection.size() * 2);
        for (T e : collection) {
            add(e);
        }
    }

    public static int roundUpToPowerOfTwo(int i) {
        // If input is a power of two, shift its high-order bit right.
        i--;

        // "Smear" the high-order bit all the way to the right.
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        i |= i >>> 16;

        return i + 1;
    }

    public static int secondaryHash(Object key) {
        int hash = key.hashCode();
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash ^= (hash >>> 7) ^ (hash >>> 4);
        return hash;
    }

    @Override
    public Iterator<T> iterator() {
        return new HashSetIterator();
    }

    @Override
    public int size() {
        return mSize;
    }

    /**
     * 删除一个元素
     * @param key 要删除的元素
     * @return 是否删除成功
     */
    @Override
    public boolean remove(Object key) {
        //删除空的元素
        if (key == null) {
            if (mEntryForNull == null) {
                return false;
            } else {
                mEntryForNull = null;
                mSize--;
                return true;
            }
        }
        //计算key在数组中的索引
        int hash = secondaryHash(key);
        SimpleHashSetEntry<T>[] tab = mTable;
        //hash值要和数组长度进行与运算,才能得到在数组中的索引位置
        int index = hash & (tab.length - 1);
        //一旦有hash计算,就有hash冲突的存在. 不同的key可能在同一个hash里.这些key会以链表的形式存放在数组的同一个索引位置
        //e表示数组索引位置中链表的第一个条目, 如果第一个条目不是我们要找的,则要循环当前条目的next指针在链表中寻找
        //一开始prev是空,因为链表表头没有前一个条目,当到下一个元素时,prev指向的就是前一个条目
        for (SimpleHashSetEntry<T> e = tab[index], prev = null; e != null; prev = e, e = e.mNext) {
            if (e.mHash == hash && key.equals(e.mKey)) {
                if (prev == null) {
                    //链表表头的第一个元素就是我们要删除的元素,因为表头条目的prev=null
                    tab[index] = e.mNext;
                } else {
                    //要删除的元素不在链表表头. 现在循环到了当前要删除的元素.
                    //则将当前元素的前一个元素的next指针指向当前元素的下一个元素.
                    //这样当前元素就被删除了,因为没有条目引用到它了.
                    prev.mNext = e.mNext;
                }
                //成功删除一个条目. 注意mSize不是数组的大小, 而是条目的大小. 因为数组的一个元素会存放多个条目
                mSize--;
                return true;
            }
        }
        //如果不满足for循环中的条件,或者for循环之后还没有找到这个元素,说明这个元素不在数组中!
        return false;
    }

    @Override
    public boolean add(T key) {
        if (key == null) {
            if (mEntryForNull == null) {
                //允许添加一个空的条目?
                mSize++;
                mEntryForNull = new SimpleHashSetEntry<T>(0, null);
                return true;
            }
            //再次添加一个空的条目,则不允许了,因为只允许有一个空的条目
            return false;
        }

        int hash = secondaryHash(key);
        SimpleHashSetEntry<T>[] tab = mTable;
        int index = hash & (tab.length - 1);
        for (SimpleHashSetEntry<T> e = tab[index]; e != null; e = e.mNext) {
            //在数组索引位置的链表中遍历, 如果key已经存在, 则不允许再次加入. 满足了set的含义
            if (e.mKey == key || (e.mHash == hash && e.mKey.equals(key))) {
                return false;
            }
        }

        // No entry for (non-null) key is present; create one
        if (mSize++ > threshold) {
            tab = doubleCapacity();
            index = hash & (tab.length - 1);
        }
        //添加到数组索引位置的链表表头
        //set不像map有value, set里的元素都是一个个的key. 所以key可以看做set里的value
        tab[index] = new SimpleHashSetEntry<T>(hash, key);
        return true;
    }

    /**
     * Allocate a table of the given capacity and set the threshold accordingly.
     *
     * @param newCapacity must be a power of two
     */
    private SimpleHashSetEntry<T>[] makeTable(int newCapacity) {
        @SuppressWarnings("unchecked")
        SimpleHashSetEntry<T>[] newTable = (SimpleHashSetEntry<T>[]) new SimpleHashSetEntry[newCapacity];
        mTable = newTable;
        threshold = (newCapacity >> 1) + (newCapacity >> 2); // 3/4 capacity
        return newTable;
    }

    /**
     * Doubles the capacity of the hash table. Existing entries are placed in
     * the correct bucket on the enlarged table. If the current capacity is,
     * MAXIMUM_CAPACITY, this method is a no-op. Returns the table, which
     * will be new unless we were already at MAXIMUM_CAPACITY.
     */
    private SimpleHashSetEntry<T>[] doubleCapacity() {
        SimpleHashSetEntry<T>[] oldTable = mTable;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        int newCapacity = oldCapacity * 2;
        SimpleHashSetEntry<T>[] newTable = makeTable(newCapacity);
        if (mSize == 0) {
            return newTable;
        }

        for (int j = 0; j < oldCapacity; j++) {
            /*
             * Rehash the bucket using the minimum number of field writes.
             * This is the most subtle and delicate code in the class.
             */
            SimpleHashSetEntry<T> e = oldTable[j];
            if (e == null) {
                continue;
            }
            int highBit = e.mHash & oldCapacity;
            SimpleHashSetEntry<T> broken = null;
            newTable[j | highBit] = e;
            for (SimpleHashSetEntry<T> n = e.mNext; n != null; e = n, n = n.mNext) {
                int nextHighBit = n.mHash & oldCapacity;
                if (nextHighBit != highBit) {
                    if (broken == null) {
                        newTable[j | nextHighBit] = n;
                    } else {
                        broken.mNext = n;
                    }
                    broken = e;
                    highBit = nextHighBit;
                }
            }
            if (broken != null)
                broken.mNext = null;
        }
        return newTable;
    }

    @Override
    public boolean contains(Object key) {
        if (key == null) {
            return mEntryForNull != null;
        }
        int hash = secondaryHash(key);
        SimpleHashSetEntry<T>[] tab = mTable;
        //是否包含key的操作和add中还没添加条目之前的操作是一样的, 都是遍历链表
        for (SimpleHashSetEntry<T> e = tab[hash & (tab.length - 1)]; e != null; e = e.mNext) {
            if (e.mKey == key || (e.mHash == hash && e.mKey.equals(key))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        if (mSize != 0) {
            Arrays.fill(mTable, null);
            mEntryForNull = null;
            mSize = 0;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        /*
         * This could be made more efficient. It unnecessarily hashes all of
         * the elements in the map.
         */
        SimpleHashSet<T> result;
        try {
            result = (SimpleHashSet<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }

        // Restore clone to empty state, retaining our capacity and threshold
        result.mEntryForNull = null;
        result.makeTable(mTable.length);
        result.mSize = 0;

        Iterator<T> it = iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    private static class SimpleHashSetEntry<T> {

        private int mHash;
        private T mKey;
        private SimpleHashSetEntry<T> mNext;

        private SimpleHashSetEntry(int hash, T key) {
            mHash = hash;
            mKey = key;
        }
    }

    private class HashSetIterator implements Iterator<T> {

        int nextIndex;
        SimpleHashSetEntry<T> nextEntry = mEntryForNull;
        SimpleHashSetEntry<T> lastEntryReturned;

        private HashSetIterator() {
            if (mEntryForNull == null) {
                SimpleHashSetEntry<T>[] tab = mTable;
                SimpleHashSetEntry<T> next = null;
                while (next == null && nextIndex < tab.length) {
                    next = tab[nextIndex++];
                }
                nextEntry = next;
            }
        }

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public T next() {

            if (nextEntry == null) {
                throw new NoSuchElementException();
            }

            SimpleHashSetEntry<T> entryToReturn = nextEntry;
            SimpleHashSetEntry<T>[] tab = mTable;
            SimpleHashSetEntry<T> next = entryToReturn.mNext;
            while (next == null && nextIndex < tab.length) {
                next = tab[nextIndex++];
            }
            nextEntry = next;
            lastEntryReturned = entryToReturn;
            return entryToReturn.mKey;
        }

        @Override
        public void remove() {
            if (lastEntryReturned == null) {
                throw new IllegalStateException();
            }
            SimpleHashSet.this.remove(lastEntryReturned.mKey);
            lastEntryReturned = null;
        }
    }
}
