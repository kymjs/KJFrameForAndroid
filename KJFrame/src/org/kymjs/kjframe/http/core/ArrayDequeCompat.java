/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.kjframe.http.core;

import java.util.ArrayDeque;

/**
 * 精简版{@link ArrayDeque}实现。 兼容旧版本Android的{@link ArrayDeque}，高性能Stack和Queue。
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class ArrayDequeCompat<E> {
    private transient E[] elements;
    private transient int head;
    private transient int tail;
    private static final int MIN_INITIAL_CAPACITY = 8;

    // ****** Array allocation and resizing utilities ******
    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>> 1);
            initialCapacity |= (initialCapacity >>> 2);
            initialCapacity |= (initialCapacity >>> 4);
            initialCapacity |= (initialCapacity >>> 8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0) // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        elements = (E[]) new Object[initialCapacity];
    }

    /**
     * Double the capacity of this deque. Call only when full, i.e., when head
     * and tail have wrapped around to become equal.
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        Object[] a = new Object[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = (E[]) a;
        head = 0;
        tail = n;
    }

    public ArrayDequeCompat() {
        elements = (E[]) new Object[16];
    }

    public ArrayDequeCompat(int numElements) {
        allocateElements(numElements);
    }

    public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException("e == null");
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }

    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException("e == null");
        elements[tail] = e;
        if ((tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    public boolean offer(E e) {
        return offerLast(e);
    }

    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    public E poll() {
        return pollFirst();
    }

    public E pollFirst() {
        int h = head;
        @SuppressWarnings("unchecked")
        E result = elements[h];
        // Element is null if deque empty
        if (result == null)
            return null;
        elements[h] = null; // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        @SuppressWarnings("unchecked")
        E result = elements[t];
        if (result == null)
            return null;
        elements[t] = null;
        tail = t;
        return result;
    }

    /**
     * Returns the number of elements in this deque.
     * 
     * @return the number of elements in this deque
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }
}
