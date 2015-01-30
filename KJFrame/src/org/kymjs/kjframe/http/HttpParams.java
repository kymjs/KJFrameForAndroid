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
package org.kymjs.kjframe.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.kymjs.kjframe.utils.StringUtils;

/**
 * Http请求中参数集<br>
 * <b>创建时间</b> 2014-8-7
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.3
 */
public class HttpParams {
    public ConcurrentHashMap<String, String> urlParams;
    public ConcurrentHashMap<String, FileWrapper> fileWraps;

    private void init(int i) {
        urlParams = new ConcurrentHashMap<String, String>(8);
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
    public HttpParams() {
        init();
    }

    /**
     * 构造器
     * 
     * @param i
     *            Http请求参数中文件的数量
     */
    public HttpParams(int i) {
        init(i);
    }

    public boolean haveFile() {
        return (fileWraps.size() != 0);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        } else {
            throw new RuntimeException("key or value is NULL");
        }
    }

    public void put(String key, byte[] file) {
        put(key, new ByteArrayInputStream(file));
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, InputStream value) {
        put(key, value, "fileName");
    }

    public void put(String key, InputStream value, String fileName) {
        if (key != null && value != null) {
            fileWraps.put(key, new FileWrapper(value, fileName, null));
        } else {
            throw new RuntimeException("key or value is NULL");
        }

    }

    public void remove(String key) {
        urlParams.remove(key);
        fileWraps.remove(key);
    }

    public String getStringParams() {
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
        return result.toString();
    }

    /*********************** httpClient method ************************************/

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getStringParams());

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
                        multipartEntity.addPart(entry.getKey(), file.fileName,
                                file.inputStream, file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(), file.fileName,
                                file.inputStream, isLast);
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

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return lparams;
    }

    /**
     * 封装一个文件参数
     * 
     * @author kymjs(kymjs123@gmail.com)
     */
    public static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName,
                String contentType) {
            this.inputStream = inputStream;
            this.contentType = contentType;
            if (StringUtils.isEmpty(fileName)) {
                this.fileName = "nofilename";
            } else {
                this.fileName = fileName;
            }
        }
    }
}
