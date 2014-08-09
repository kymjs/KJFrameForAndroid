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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.kymjs.aframe.utils.FileUtils;

import android.os.AsyncTask;

/**
 * The HttpLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-14
 */
public class KJHttp {

    private HttpConfig config;

    /**
     * 使用参数传递的配置器创建httpLibrary
     */
    public KJHttp(HttpConfig config) {
        this.config = config;
    }

    /**
     * 使用默认http配置创建httpLibrary
     */
    public KJHttp() {
        this(new HttpConfig());
    }

    /*********************** HttpURLConnection get请求 *************************/

    /**
     * 使用HttpURLConnection方式发起get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集,可选类型：KJFileParams、KJStringParams
     * @param callback
     *            请求中的回调方法，可选类型：FileCallBack、StringCallBack
     */
    public void urlGet(String url, KJStringParams params, I_HttpRespond callback) {
        if (params != null) {
            StringBuilder str = new StringBuilder(url);
            str.append("?").append(params.toString());
            url = str.toString();
        }
        urlGet(url, callback);
    }

    /**
     * 使用HttpURLConnection方式发起get请求
     * 
     * @param url
     *            地址
     * @param callback
     *            请求中的回调方法，可选类型：FileCallBack、StringCallBack
     */
    public void urlGet(String url, I_HttpRespond callback) {
        new HttpUrlGetTask(callback).execute(url);
    }

    /**
     * 实现HttpUrlGet请求的任务
     */
    private class HttpUrlGetTask extends AsyncTask<Object, Object, Object> {
        private I_HttpRespond callback;

        public HttpUrlGetTask(I_HttpRespond callback) {
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object... params) {
            InputStream input = null;
            BufferedReader reader = null;
            StringBuilder respond = null;
            try {
                URL url = new URL(params[0].toString());
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setUseCaches(config.isUseCache());
                conn.setReadTimeout(config.getReadTimeout());
                conn.setConnectTimeout(config.getConnectTimeOut());
                conn.setRequestProperty("Charset", config.getCharSet());
                conn.setRequestMethod("GET");
                input = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                int i = 0, current = 0;
                int count = conn.getContentLength();
                char[] buf = new char[1024];
                respond = new StringBuilder();
                while ((i = reader.read(buf)) != -1) {
                    respond.append(buf, 0, i);
                    if (callback.isProgress()) {
                        current += i;
                        // 每次循环调用一次
                        publishProgress(count, current);
                    }
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                return e;
            } catch (IOException e) {
                return e;
            } finally {
                FileUtils.closeIO(input, reader);
            }
            return respond;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            callback.onLoading((Long) values[0], (Long) values[1]);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result instanceof MalformedURLException) {
                callback.onFailure((Throwable) result, 3721, "URL错误");
            } else if (result instanceof IOException) {
                callback.onFailure((Throwable) result, 3722, "IO错误");
            } else {
                callback.onSuccess(result);
            }
        }
    }

    /*********************** HttpURLConnection post请求 *************************/

    /**
     * 使用HttpURLConnection方式发起post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集,可选类型：KJFileParams、KJStringParams
     * @param callback
     *            请求中的回调方法，可选类型：FileCallBack、StringCallBack
     */
    public void urlPost(String url, I_HttpParams params, I_HttpRespond callback) {
        if (params instanceof KJStringParams) {
            new HttpUrlPostTask(params, callback).execute(url);
        } else if (params instanceof KJFileParams) {
            new HttpUrlFileTask((KJFileParams) params, callback).execute(url);
        }
    }

    /**
     * 实现HttpUrlFile请求的任务
     */
    private class HttpUrlFileTask extends AsyncTask<Object, Object, Object> {
        private I_HttpRespond callback;
        private KJFileParams params;

        public HttpUrlFileTask(KJFileParams param, I_HttpRespond callback) {
            this.callback = callback;
            this.params = param;
        }

        @Override
        protected Object doInBackground(Object... urls) {
            OutputStream out = null;
            DataInputStream in = null;
            BufferedReader reader = null;
            StringBuilder respond = null;
            String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
            try {
                URL url = new URL(urls[0].toString());
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                // 发送POST请求必须设置如下两行
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setReadTimeout(config.getReadTimeout());
                conn.setConnectTimeout(config.getConnectTimeOut());
                conn.setRequestProperty("Charset", config.getCharSet());
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + BOUNDARY);
                out = new DataOutputStream(conn.getOutputStream());
                byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
                int leng = params.fileParams.size();
                for (int i = 0; i < leng; i++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--")
                            .append(BOUNDARY)
                            .append("\r\nContent-Disposition: form-data;name=\"KJFrameForAndroid_File"
                                    + i
                                    + "\";filename=\"KJFrameForAndroid_File\"\r\n")
                            .append("Content-Type:application/octet-stream\r\n\r\n");
                    byte[] data = sb.toString().getBytes();
                    out.write(data);
                    in = new DataInputStream(params.fileParams.get(i));
                    int bytes = 0;
                    byte[] buf = new byte[1024];
                    while ((bytes = in.read(buf)) != -1) {
                        out.write(buf, 0, bytes);
                    }
                    out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
                }
                out.write(end_data);
                out.flush();

                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                respond = new StringBuilder();
                int i = 0;
                int current = 0;
                int count = conn.getContentLength();
                char[] buf = new char[512];
                while ((i = reader.read(buf)) != -1) {
                    respond.append(buf, 0, i);
                    if (callback.isProgress()) {
                        current += i;
                        // 每次循环调用一次
                        publishProgress(count, current);
                    }
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                return e;
            } catch (IOException e) {
                return e;
            } finally {
                FileUtils.closeIO(out, in, reader);
            }
            return respond;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            callback.onLoading((Long) values[0], (Long) values[1]);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result instanceof MalformedURLException) {
                callback.onFailure((Throwable) result, 3721, "URL错误");
            } else if (result instanceof IOException) {
                callback.onFailure((Throwable) result, 3722, "IO错误");
            } else {
                callback.onSuccess(result);
            }
        }
    }

    /**
     * 实现HttpUrlPost请求的任务
     */
    private class HttpUrlPostTask extends AsyncTask<Object, Object, Object> {
        private I_HttpRespond callback;
        private I_HttpParams params;

        public HttpUrlPostTask(I_HttpParams param, I_HttpRespond callback) {
            this.callback = callback;
            this.params = param;
        }

        @Override
        protected Object doInBackground(Object... urls) {
            DataOutputStream out = null;
            InputStream input = null;
            BufferedReader reader = null;
            StringBuilder respond = null;
            try {
                URL url = new URL(urls[0].toString());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setReadTimeout(config.getReadTimeout());
                connection.setConnectTimeout(config.getConnectTimeOut());
                connection.setRequestProperty("Charset", config.getCharSet());
                connection.setRequestProperty("Content-Type",
                        config.getContentType());
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
                respond = new StringBuilder();
                int i = 0;
                int current = 0;
                int count = connection.getContentLength();
                char[] buf = new char[1024];
                while ((i = reader.read(buf)) != -1) {
                    respond.append(buf, 0, i);
                    if (callback.isProgress()) {
                        current += i;
                        // 每次循环调用一次
                        publishProgress(count, current);
                    }
                }
                connection.disconnect();
            } catch (MalformedURLException e) {
                return e;
            } catch (IOException e) {
                return e;
            } finally {
                FileUtils.closeIO(out, input, reader);
            }
            return respond;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            callback.onLoading((Long) values[0], (Long) values[1]);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result instanceof MalformedURLException) {
                callback.onFailure((Throwable) result, 3721, "URL错误");
            } else if (result instanceof IOException) {
                callback.onFailure((Throwable) result, 3722, "IO错误");
            } else {
                callback.onSuccess(result);
            }
        }
    }

}
