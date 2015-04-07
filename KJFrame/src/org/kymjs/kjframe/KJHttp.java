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
package org.kymjs.kjframe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.core.CachedTask;
import org.kymjs.kjframe.http.download.I_FileLoader;
import org.kymjs.kjframe.http.download.SimpleDownloader;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.StringUtils;

/**
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class KJHttp {
    public static final String BOUNDARY = "---------7d4a6d158c9"; // 定义http上传文件的数据分隔线

    private enum Method {
        UNKNOW, GET, POST
    }

    private HttpConfig httpConfig;
    private I_FileLoader downloader;

    public KJHttp() {
        this(null);
    }

    public KJHttp(HttpConfig config) {
        if (config == null) {
            httpConfig = new HttpConfig();
        } else {
            this.httpConfig = config;
        }
    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    /**
     * 自定义配置
     * 
     * @param httpConfig
     */
    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    /**
     * 使用HttpURLConnection方式发起get请求
     * 
     * @param url
     *            地址
     * @param callback
     *            请求中的回调方法
     */
    public void get(String url, HttpCallBack callback) {
        get(url, null, callback);
    }

    /**
     * 使用HttpURLConnection方式发起get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public void get(String url, HttpParams params, HttpCallBack callback) {
        if (params != null) {
            StringBuilder str = new StringBuilder(url);
            str.append("?").append(params.toString());
            url = str.toString();
        }
        VolleyTask.setDefaultExecutor(VolleyTask.mLruSerialExecutor);
        new VolleyTask(Method.GET, url, null, callback).execute();
    }

    /**
     * 使用HttpURLConnection方式发起post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public void post(String url, HttpParams params, HttpCallBack callback) {
        VolleyTask.setDefaultExecutor(VolleyTask.mLruSerialExecutor);
        if (params != null) {
            new VolleyTask(Method.POST, url, params, callback).execute();
        } else {
            new VolleyTask(Method.GET, url, null, callback).execute();
        }
    }

    /**
     * 文件下载
     * 
     * @param url
     *            地址
     * @param save
     *            保存位置
     * @param callback
     */
    public void download(String url, File save, HttpCallBack callback) {
        download(url, save, new SimpleDownloader(httpConfig, callback),
                callback);
    }

    /**
     * 自定义文件下载器下载
     * 
     * @param url
     *            地址
     * @param save
     *            保存位置
     * @param downloader
     *            下载器
     * @param callback
     */
    public void download(String url, File save, I_FileLoader downloader,
            HttpCallBack callback) {
        try {
            if (!save.exists()) {
                save.createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException("save file can not create");
        }
        this.downloader = downloader;
        httpConfig.savePath = save;
        downloader.doDownload(url, true);
    }

    /**
     * 暂停下载
     */
    public void stopDownload() {
        if (downloader != null) {
            downloader.stop();
        }
    }

    /**
     * 是否已经停止下载
     */
    public boolean isStopDownload() {
        if (downloader != null) {
            return downloader.isStop();
        } else {
            return false;
        }
    }

    /**
     * 移除一份磁盘缓存
     * 
     * @param uri
     *            接口地址
     * @param params
     *            http请求时的参数，如果没有则传null
     */
    public void removeDiskCache(String uri, HttpParams params) {
        VolleyTask.remove(uri + params); // post请求时的key
        if (params != null) {
            VolleyTask.remove(uri + "?" + params + "null"); // get请求时的key
        }
    }

    /**
     * 清空缓存(本操作是异步处理，不会卡顿UI)
     */
    public void removeAllDiskCache() {
        VolleyTask.cleanCacheFiles(HttpConfig.CACHEPATH);
    }

    /**
     * 读取一份缓存数据，不考虑缓存是否已过期
     * 
     * @return 如果不存在，则为null
     */
    public String getCache(String uri, HttpParams params) {
        String cache = null;
        if (params == null) {
            cache = VolleyTask.getCache(HttpConfig.CACHEPATH, uri + params);
        } else {
            cache = VolleyTask.getCache(HttpConfig.CACHEPATH, uri + "?"
                    + params + "null");
        }
        return cache;
    }

    /**
     * 自带数据缓存功能，且各部分均可捕获异常,可处理#{@link org.kymjs.kjlibrary.KJHttp.Method}
     * 中定义的http请求类型
     */
    public class VolleyTask extends CachedTask<String, Object, String> {
        private int respondCode = -1;
        private String respondMsg = "";

        private Method requestMethod;
        private String uri;
        private HttpParams params;
        private HttpCallBack callback;
        private String charsetName;

        private VolleyTask(String cachePath, String key, long cacheTime) {
            super(cachePath, key, cacheTime);
        }

        public VolleyTask(Method requestMethod, String uri, HttpParams params,
                HttpCallBack callback) {
            super(HttpConfig.CACHEPATH, uri
                    + (params == null ? "null" : "?" + params + "null"),
                    httpConfig.cacheTime);
            this.requestMethod = requestMethod;
            this.uri = uri;
            this.params = params;
            this.callback = callback;

            this.charsetName = httpConfig.httpHeader.get("Charset");
            if (StringUtils.isEmpty(charsetName)) {
                this.charsetName = "UTF-8";
            }
            String cookie = httpConfig.httpHeader.get("Cookie");
            if (StringUtils.isEmpty(cookie)) {
                httpConfig.httpHeader.put("cookie",
                        httpConfig.getCookieString());
            }

            if (httpConfig.useDelayCache) {
                setDelayCacheTime(httpConfig.delayTime);
            }
        }

        @Override
        protected void onPreExecuteSafely() throws Exception {
            super.onPreExecuteSafely();
            callback.onPreStart();
        }

        @Override
        protected String doConnectNetwork(String... uris) throws Exception { // 参数uris留待以后使用
            InputStream input = null;
            BufferedReader reader = null;
            StringBuilder respond = new StringBuilder();

            try {
                HttpURLConnection conn = openConnection(requestMethod, uri,
                        params, charsetName);
                respondMsg = conn.getResponseMessage();
                respondCode = conn.getResponseCode();
                input = conn.getInputStream();
                httpConfig.respondHeader = conn.getHeaderFields();
                callback.onHttpConnection(conn);
                reader = new BufferedReader(new InputStreamReader(input,
                        charsetName));
                int len = 0;
                char[] buf = new char[1024];
                while ((len = reader.read(buf)) != -1) {
                    respond.append(buf, 0, len);
                }
                conn.disconnect();
            } finally {
                FileUtils.closeIO(input, reader);
            }
            return respond.toString();
        }

        @Override
        protected void onPostExecuteSafely(String result, Exception e)
                throws Exception {
            super.onPostExecuteSafely(result, e);
            callback.respondCode = this.respondCode;
            if (e == null) {
                if (resFromCache) {
                    callback.onSuccessFromCache(respondCode, result);
                } else {
                    callback.onSuccess(respondCode, result);
                }
            } else {
                callback.onFailure(e, respondCode, respondMsg);
            }
            callback.onFinish();
        }

    }

    /**
     * Opens an {@link HttpURLConnection} with parameters.
     * 
     * @param url
     * @return an open connection
     * @throws IOException
     */
    private HttpURLConnection openConnection(Method requestMethod, String uri,
            HttpParams params, String charsetName) throws IOException {

        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(httpConfig.timeOut);
        connection.setReadTimeout(httpConfig.timeOut);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        switch (requestMethod) {
        case GET:
            connection.setRequestMethod("GET");
            break;
        case POST:
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            break;
        default:
            new IllegalStateException("unsupported http request method");
            break;
        }

        for (String headerName : httpConfig.httpHeader.keySet()) {
            connection.addRequestProperty(headerName,
                    httpConfig.httpHeader.get(headerName));
        }

        if (requestMethod == Method.POST && params != null) {
            DataInputStream in = null;
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(connection.getOutputStream());
                byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n")
                        .getBytes(charsetName);// 定义最后数据分隔线

                StringBuilder sb = new StringBuilder();
                boolean isFirst = true;
                for (Map.Entry<String, String> entry : params.urlParams
                        .entrySet()) {
                    if (!isFirst) {
                        out.write("\r\n".getBytes(charsetName));
                    } else {
                        isFirst = false;
                    }
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\nContent-Disposition: form-data; name=\"");
                    sb.append(entry.getKey());
                    sb.append("\"\r\n\r\n");
                    sb.append(entry.getValue());
                    out.write(sb.toString().getBytes(charsetName));
                    sb.delete(0, sb.length());
                }

                isFirst = true;
                for (Map.Entry<String, HttpParams.FileWrapper> entry : params.fileWraps
                        .entrySet()) {
                    if (!isFirst) {
                        out.write("\r\n".getBytes(charsetName));
                    } else {
                        isFirst = false;
                    }
                    sb.append("--")
                            .append(BOUNDARY)
                            .append("\r\nContent-Disposition: form-data;name=\"")
                            .append(entry.getKey())
                            .append("\";filename=\"")
                            .append(entry.getValue().fileName)
                            .append("\"\r\nContent-Type:application/octet-stream\r\n\r\n");
                    byte[] data = sb.toString().getBytes(charsetName);
                    out.write(data);
                    in = new DataInputStream(entry.getValue().inputStream);
                    int bytes = 0;
                    byte[] buf = new byte[1024];
                    while ((bytes = in.read(buf)) != -1) {
                        out.write(buf, 0, bytes);
                    }
                    sb.delete(0, sb.length());
                }
                out.write(end_data);
                out.flush();
            } finally {
                FileUtils.closeIO(out);
            }
        }
        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol())
                && httpConfig.sslSocketFactory != null) {
            ((HttpsURLConnection) connection)
                    .setSSLSocketFactory(httpConfig.sslSocketFactory);
        }
        return connection;
    }
}
