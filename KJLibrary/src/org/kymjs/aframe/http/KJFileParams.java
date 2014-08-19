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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.kymjs.aframe.core.KJException;

/**
 * http请求中如果包含文件参数，应该使用该类的对象作为kjh.urlPost()方法的参数<br>
 * 
 * <b>说明</b> 虽然你可以不论参数是否包含文件都使用该类对象作为kjh.urlPost()方法的参数，
 * 但为了效率你应该为没有文件参数的kjh.urlPost()方法传递KJStringParams对象 <br>
 * <b>说明</b> 该类使用一个ConcurrentHashMap(String, String)保存字符串类型的参数，
 * 使用一个ArrayList(InputStream)保存文件类型的参数<br>
 * <b>创建时间</b> 2014-8-7
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.2
 */
public class KJFileParams implements I_HttpParams {
    private ConcurrentHashMap<String, String> urlParams;
    // 该对象仅在使用HttpUrlConnection时会用到
    protected ArrayList<InputStream> fileParams;
    // 该对象仅在使用HttpClient时会用到
    protected ConcurrentHashMap<String, FileWrapper> fileWraps;

    private void init(int i) {
        urlParams = new ConcurrentHashMap<String, String>(8);
        fileParams = new ArrayList<InputStream>(i);
        fileWraps = new ConcurrentHashMap<String, FileWrapper>(i);
    }

    private void init() {
        init(4);
    }

    /**
     * 构造器
     * 
     * <br>
     * <b>说明</b> 为提高效率默认使用4个文件作为List大小
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
        put(HttpConfig.FileParamsKey + fileWraps.size(), value,
                HttpConfig.FileParamsName);
    }

    public void put(String key, InputStream value, String fileName) {
        if (value != null) {
            fileParams.add(value);
            if (key != null) {
                fileWraps.put(key, new FileWrapper(value, fileName, null));
            }
        } else {
            throw new KJException("value is NULL");
        }

    }

    public void remove(String key) {
        urlParams.remove(key);
        fileWraps.remove(key);
        fileParams.remove(key);
    }

    /*********************** httpClient method ************************************/

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileWraps
                .entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }
        return result.toString();
    }

    /**
     * 获取参数集
     */
    @Override
    public HttpEntity getEntity() {
        HttpEntity entity = null;
        if (!fileWraps.isEmpty()) {
            MultipartEntity multipartEntity = new MultipartEntity();
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                    .entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }
            int currentIndex = 0;
            int lastIndex = fileWraps.entrySet().size() - 1;
            for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileWraps
                    .entrySet()) {
                FileWrapper file = entry.getValue();
                if (file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if (file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(),
                                file.getFileName(), file.inputStream,
                                file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(),
                                file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }
            entity = multipartEntity;
        } else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    /**
     * String的参数集，如果参数仅有String而没有File时，为了效率你应该使用KJStringParams
     */
    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return lparams;
    }

    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), "UTF-8");
    }

    /**
     * 封装一个文件参数
     * 
     * @author kymjs(kymjs123@gmail.com)
     */
    private static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName,
                String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if (fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }
}
