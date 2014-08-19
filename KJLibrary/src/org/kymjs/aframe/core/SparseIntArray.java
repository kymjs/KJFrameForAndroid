/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.aframe.core;

/**
 * 一个key值不可重复的有序k(int)-v(int)对集合类，使用一个从大到小的int数组存储key值，
 * 它的目的是用于替换HashMap(Integer, Integer)<br>
 * 
 * <b>创建时间</b> 2014-8-9
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class SparseIntArray implements Cloneable {

    private int[] mKeys;
    private int[] mValues;
    private int mSize;

    /**
     * 默认创建十个大小的长度
     */
    public SparseIntArray() {
        this(10);
    }

    /**
     * 使用指定的空间创建数组，这个空间无法自动扩容
     * 
     * @param initialCapacity
     *            数组空间大小
     */
    public SparseIntArray(int initialCapacity) {
        initialCapacity = ArrayUtils.idealIntArraySize(initialCapacity);

        mKeys = new int[initialCapacity];
        mValues = new int[initialCapacity];
        mSize = 0;
    }

    @Override
    public SparseIntArray clone() {
        SparseIntArray clone = null;
        try {
            clone = (SparseIntArray) super.clone();
            clone.mKeys = mKeys.clone();
            clone.mValues = mValues.clone();
        } catch (CloneNotSupportedException cnse) {
        }
        return clone;
    }

    /**
     * 获取key所对应的value，如果不存在则返回0
     */
    public int get(int key) {
        return get(key, 0);
    }

    /**
     * 获取key所对应的value，如果不存在则返回默认值
     * 
     * @param key
     *            指定的key
     * @param defValue
     *            如果key所对应的value不存在，则返回该值
     */
    public int get(int key, int defValue) {
        int i = binarySearch(mKeys, 0, mSize, key);
        if (i < 0) {
            return defValue;
        } else {
            return mValues[i];
        }
    }

    /**
     * 移除key对应的value
     */
    public void delete(int key) {
        int i = binarySearch(mKeys, 0, mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    /**
     * 移除数组中一个指定的下标
     */
    public void removeAt(int index) {
        System.arraycopy(mKeys, index + 1, mKeys, index, mSize - (index + 1));
        System.arraycopy(mValues, index + 1, mValues, index, mSize
                - (index + 1));
        mSize--;
    }

    /**
     * 添加一个数据段
     */
    public void put(int key, int value) {
        int i = binarySearch(mKeys, 0, mSize, key);
        if (i >= 0) {
            mValues[i] = value;
        } else {
            i = ~i;
            if (mSize >= mKeys.length) {
                int n = ArrayUtils.idealIntArraySize(mSize + 1);
                int[] nkeys = new int[n];
                int[] nvalues = new int[n];
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                System.arraycopy(mValues, 0, nvalues, 0, mValues.length);
                mKeys = nkeys;
                mValues = nvalues;
            }
            if (mSize - i != 0) {
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
                System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
            }
            mKeys[i] = key;
            mValues[i] = value;
            mSize++;
        }
    }

    /**
     * 返回当前集合中的数据大小
     */
    public int size() {
        return mSize;
    }

    /**
     * 返回index对应的key值
     * 
     * @param index
     *            下标值
     */
    public int keyAt(int index) {
        return mKeys[index];
    }

    /**
     * 返回index对应的value值
     * 
     * @param index
     *            下标值
     */
    public int valueAt(int index) {
        return mValues[index];
    }

    /**
     * 返回key所在下标位置
     */
    public int indexOfKey(int key) {
        return binarySearch(mKeys, 0, mSize, key);
    }

    /**
     * 返回value所在下标位置，如果value不存在，则返回-1
     */
    public int indexOfValue(int value) {
        for (int i = 0; i < mSize; i++) {
            if (mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 移除全部集合数据
     */
    public void clear() {
        mSize = 0;
    }

    /**
     * Puts a key/value pair into the array, optimizing for the case where the
     * key is greater than all existing keys in the array.
     */
    public void append(int key, int value) {
        if (mSize != 0 && key <= mKeys[mSize - 1]) {
            put(key, value);
            return;
        }

        int pos = mSize;
        if (pos >= mKeys.length) {
            int n = ArrayUtils.idealIntArraySize(pos + 1);

            int[] nkeys = new int[n];
            int[] nvalues = new int[n];

            System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
            System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

            mKeys = nkeys;
            mValues = nvalues;
        }

        mKeys[pos] = key;
        mValues[pos] = value;
        mSize = pos + 1;
    }

    /**
     * 使用折半查找算法，查找出key所应该插入的index，如果key存在，则返回key当前的位置
     * 
     * @param a
     *            要查找的数组
     * @param start
     *            查找起始位置
     * @param len
     *            查找的长度
     * @param key
     *            比较的值
     */
    private static int binarySearch(int[] a, int start, int len, int key) {
        // 计算起始与结束位置
        int high = start + len, low = start - 1, guess;
        // 只要起始与结束不是同一个点
        while (high - low > 1) {
            guess = (high + low) / 2;
            if (a[guess] < key)
                low = guess;
            else
                high = guess;
        }
        if (high == start + len)
            return ~(start + len);
        else if (a[high] == key)
            return high;
        else
            return ~high;
    }
}
