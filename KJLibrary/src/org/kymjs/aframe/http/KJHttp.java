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

    public KJHttp(HttpConfig config) {
        this.config = config;
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
        new HttpGetTask(callback).execute(url);
    }

    private class HttpGetTask extends AsyncTask<Object, Object, Object> {
        private I_HttpRespond callback;

        public HttpGetTask(I_HttpRespond callback) {
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
    public void urlPost(String url, KJParams params, I_HttpRespond callback) {
        new HttpPostTask(params, callback).execute(url);
    }

    private class HttpPostTask extends AsyncTask<Object, Object, Object> {
        private I_HttpRespond callback;
        private KJParams params;

        public HttpPostTask(KJParams param, I_HttpRespond callback) {
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
                        "application/x-www-form-urlencoded");
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.connect();
                out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(params.toString());
                out.flush();

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
