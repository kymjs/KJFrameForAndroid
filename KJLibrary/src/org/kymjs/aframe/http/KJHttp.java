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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.kymjs.aframe.ThreadPoolManager;
import org.kymjs.aframe.utils.FileUtils;

/**
 * The HttpLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-14
 */
public class KJHttp {

    private ThreadPoolManager pool;
    private HttpConfig config;

    public KJHttp(HttpConfig config) {
        this.config = config;
        pool = ThreadPoolManager.create();
    }

    public KJHttp() {
        this(new HttpConfig());
    }

    /*********************** HttpURLConnection get请求 *************************/

    public void urlGet(String url, KJParams params, I_HttpRespond callback) {
        if (params != null) {
            StringBuilder str = new StringBuilder(url);
            str.append("?").append(params.toString());
            url = str.toString();
        }
        urlGet(url, callback);
    }

    public void urlGet(String url, I_HttpRespond callback) {
        pool.addTask(getGetHttpThread(url, callback));
    }

    /**
     * 获取一个Get请求线程
     * 
     * @param _url
     *            网络地址
     * @param callback
     *            回调接口
     */
    private Runnable getGetHttpThread(final String _url,
            final I_HttpRespond callback) {
        return new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(_url);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setUseCaches(config.isUseCache());
                    conn.setReadTimeout(config.getReadTimeout());
                    conn.setConnectTimeout(config.getConnectTimeOut());
                    conn.setRequestProperty("Charset", config.getCharSet());
                    conn.setRequestMethod("GET");
                    input = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder respond = new StringBuilder();
                    int i = 0;
                    int current = 0;
                    int count = conn.getContentLength();
                    char[] buf = new char[1024];
                    while ((i = reader.read(buf)) != -1) {
                        respond.append(buf, 0, i);
                        // 每次循环调用一次
                        if (callback.progress) {
                            current += i;
                            callback.onLoading(count, current);
                        }
                    }
                    callback.onSuccess(respond);
                } catch (MalformedURLException e) {
                    callback.onFailure(e, 3721, "URL错误");
                } catch (IOException e) {
                    callback.onFailure(e, 3722, "IO错误");
                } finally {
                    FileUtils.closeIO(input, reader);
                }
            }
        };
    }

    /*********************** HttpURLConnection post请求 *************************/
    public void urlPost(String url, KJParams params, I_HttpRespond callback) {
        pool.addTask(getPostHttpThread(url, params, callback));
    }

    private Runnable getPostHttpThread(final String _url,
            final KJParams params, final I_HttpRespond callback) {
        return new Runnable() {
            @Override
            public void run() {
                DataOutputStream out = null;
                InputStream input = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(_url);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setReadTimeout(config.getReadTimeout());
                    connection.setConnectTimeout(config.getConnectTimeOut());
                    connection.setRequestProperty("Charset",
                            config.getCharSet());
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setInstanceFollowRedirects(true);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);
                    connection.connect();
                    if (params != null) {
                        out = new DataOutputStream(connection.getOutputStream());
                        out.writeBytes(params.toString());
                        out.flush();
                    }

                    input = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder respond = new StringBuilder();
                    int i = 0;
                    int current = 0;
                    int count = connection.getContentLength();
                    char[] buf = new char[1024];
                    while ((i = reader.read(buf)) != -1) {
                        respond.append(buf, 0, i);
                        // 每次循环调用一次
                        if (callback.progress) {
                            current += i;
                            callback.onLoading(count, current);
                        }
                    }
                    callback.onSuccess(respond);
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    callback.onFailure(e, 3721, "URL错误");
                } catch (IOException e) {
                    callback.onFailure(e, 3722, "IO错误");
                } finally {
                    FileUtils.closeIO(out, input, reader);
                }
            }
        };
    }

}
