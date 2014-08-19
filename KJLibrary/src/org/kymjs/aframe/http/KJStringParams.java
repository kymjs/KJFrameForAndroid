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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.kymjs.aframe.core.KJException;

/**
 * 存储http请求中纯字符参数，如果你的http请求参数中包含了文件或数据流，你应该使用KJFileParams的对象作为kjh.urlPost()
 * 方法的参数<br>
 * 
 * 
 * <b>说明</b> 虽然你可以不论参数是否包含文件都使用该类对象作为kjh.urlPost()方法的参数，
 * 但为了效率你应该为没有文件参数的kjh.urlPost()方法传递KJStringParams对象 <br>
 * <b>说明</b> 该类使用一个ConcurrentHashMap(String, String)保存字符串类型的参数<br>
 * <b>创建时间</b> 2014-8-7
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 */
public class KJStringParams implements I_HttpParams {
    protected ConcurrentHashMap<String, String> urlParams;

    protected void init() {
        urlParams = new ConcurrentHashMap<String, String>(6);
    }

    public KJStringParams() {
        init();
    }

    public KJStringParams(Map<String, String> source) {
        init();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public KJStringParams(String key, String value) {
        init();
        put(key, value);
    }

    /**
     * 添加一个指定的键值对，若k或v为null，则抛出KJException异常
     * 
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        } else {
            throw new KJException("key or value is NULL");
        }
    }

    /**
     * 移除key与对应的value
     */
    public void remove(String key) {
        if (key == null) {
            throw new KJException("key or value is NULL");
        } else {
            urlParams.remove(key);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        Iterator<Entry<String, String>> it = urlParams.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value;
            try {
                value = URLEncoder.encode(entry.getValue(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                value = entry.getValue();
            }
            str.append(key).append("=").append(value).append("&");
        }
        return str.toString();
    }

    /**
     * 获取参数集，这里只是为了方便httpClient使用，如果是HttpUrlConnection直接调用toString()就行了
     */
    @Override
    public HttpEntity getEntity() {
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(getParamsList(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return lparams;
    }
}