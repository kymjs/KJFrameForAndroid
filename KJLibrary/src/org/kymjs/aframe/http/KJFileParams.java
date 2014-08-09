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
package org.kymjs.aframe.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.kymjs.aframe.KJException;

/**
 * http请求中如果包含文件参数，应该使用该类的对象作为kjh.urlPost()方法的参数
 * 
 * @explain 虽然你可以不论参数是否包含文件都使用该类对象作为kjh.urlPost()方法的参数，
 *          但为了效率你应该为没有文件参数的kjh.urlPost()方法传递KJStringParams对象
 * @explain 该类使用一个ConcurrentHashMap<String, String>保存字符串类型的参数，
 *          使用一个ArrayList<InputStream>保存文件类型的参数
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-8-7
 */
public class KJFileParams implements I_HttpParams {
    private ConcurrentHashMap<String, String> urlParams;
    public ArrayList<InputStream> fileParams;

    private void init(int i) {
        urlParams = new ConcurrentHashMap<String, String>(6);
        fileParams = new ArrayList<InputStream>(i);
    }

    private void init() {
        init(3);
    }

    /**
     * 构造器
     * 
     * @explain 为提高效率默认使用3个文件作为List大小
     */
    public KJFileParams() {
        init();
    }

    /**
     * 构造器
     * 
     * @param i
     *            http请求参数中文件的数量
     */
    public KJFileParams(int i) {
        init(i);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        } else {
            throw new KJException("key or value is NULL");
        }
    }

    public void put(byte[] file) {
        put(new ByteArrayInputStream(file));
    }

    public void put(File file) throws FileNotFoundException {
        put(new FileInputStream(file));
    }

    public void put(InputStream value) {
        if (value != null) {
            fileParams.add(value);
        } else {
            throw new KJException("key or value is NULL");
        }
    }
}
