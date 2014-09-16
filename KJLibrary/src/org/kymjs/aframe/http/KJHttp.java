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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.kymjs.aframe.core.KJException;
import org.kymjs.aframe.core.KJTaskExecutor;
import org.kymjs.aframe.core.KJThreadExecutors;
import org.kymjs.aframe.http.cache.HttpCache;
import org.kymjs.aframe.http.downloader.FileDownLoader;
import org.kymjs.aframe.http.downloader.I_FileLoader;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;

/**
 * update log
 * 1.1 添加httpUrlConnection请求操作
 * 1.2 添加httpUrlConnection下载操作
 * 1.3 添加httpClient的post、get、put等方式请求
 * 1.4 添加http请求中对json(应该是一切字符串)数据的缓存
 * 1.5 添加Http请求中对请求头的自定义以及cookie的定义
 */

/**
 * The HttpLibrary's core classes<br>
 * 
 * <b>创建时间</b> 2014-7-14<br>
 * <b>修改时间</b> 2014-9-3
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.4
 */
public class KJHttp {

    private HttpConfig config;

    /**
     * 使用参数传递的配置器创建httpLibrary
     */
    public KJHttp(HttpConfig config) {
        this.config = config;
        if (config.getCacher() == null) {
            // json数据缓存器，也可以自己通过实现I_HttpCache接口协议定义
            this.config.setCacher(HttpCache.create());
        }
        // 如果使用httpClient必须初始化，如果不使用，则无需调用
        initHttpClient();
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
    public void urlGet(String url, KJStringParams params,
            I_HttpRespond callback) {
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
        new HttpUrlGetTask(callback, url).execute();
    }

    /**
     * 实现HttpUrlGet请求的任务
     */
    private class HttpUrlGetTask extends
            KJTaskExecutor<Void, Object, Object> {
        private I_HttpRespond callback;
        private String _url;

        public HttpUrlGetTask(I_HttpRespond callback, String _url) {
            this.callback = callback;
            this._url = _url;
        }

        @Override
        protected Object doInBackground(Void... params) {
            String res = config.getCacher().get(_url);
            if (res != null && config.isUseCache()) { // 如果有缓存
                return res;
            } else {
                InputStream input = null;
                BufferedReader reader = null;
                StringBuilder respond = null;
                try {
                    URL url = new URL(_url);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setUseCaches(config.isUseCache());
                    conn.setReadTimeout(config.getReadTimeout());
                    conn.setConnectTimeout(config.getConnectTimeOut());
                    conn.setRequestProperty("Charset",
                            config.getCharSet());
                    conn.setRequestMethod("GET");
                    String cookie = config.getCookie();
                    if (!StringUtils.isEmpty(cookie)) {
                        conn.setRequestProperty("Cookie", cookie);
                    }
                    for (Map.Entry<String, String> entry : config
                            .getHeader().entrySet()) {
                        conn.setRequestProperty(entry.getKey(),
                                entry.getValue());
                    }
                    input = conn.getInputStream();
                    reader = new BufferedReader(
                            new InputStreamReader(input));
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
                config.getCacher().add(_url, result.toString());
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
    public void urlPost(String url, I_HttpParams params,
            I_HttpRespond callback) {
        if (params instanceof KJStringParams) {
            new HttpUrlPostTask(params, callback, url).execute();
        } else if (params instanceof KJFileParams) {
            new HttpUrlFileTask((KJFileParams) params, callback)
                    .execute(url);
        }
    }

    /**
     * 实现HttpUrlFile请求的任务
     */
    private class HttpUrlFileTask extends
            KJTaskExecutor<Object, Object, Object> {
        private I_HttpRespond callback;
        private KJFileParams params;

        public HttpUrlFileTask(KJFileParams param,
                I_HttpRespond callback) {
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
                conn.setRequestProperty("Charset",
                        config.getCharSet());
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + BOUNDARY);
                String cookie = config.getCookie();
                if (!StringUtils.isEmpty(cookie)) {
                    conn.setRequestProperty("Cookie", cookie);
                }
                for (Map.Entry<String, String> entry : config
                        .getHeader().entrySet()) {
                    conn.setRequestProperty(entry.getKey(),
                            entry.getValue());
                }
                out = new DataOutputStream(conn.getOutputStream());
                byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n")
                        .getBytes();// 定义最后数据分隔线
                int leng = params.fileParams.size();
                for (int i = 0; i < leng; i++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--")
                            .append(BOUNDARY)
                            .append("\r\nContent-Disposition: form-data;name=\"")
                            .append(HttpConfig.FileParamsKey)
                            .append(i)
                            .append("\";filename=\"")
                            .append(HttpConfig.FileParamsName)
                            .append("\"\r\nContent-Type:application/octet-stream\r\n\r\n");
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
    private class HttpUrlPostTask extends
            KJTaskExecutor<Void, Object, Object> {
        private I_HttpRespond callback;
        private I_HttpParams params;
        private String _url;

        public HttpUrlPostTask(I_HttpParams param,
                I_HttpRespond callback, String _url) {
            this.callback = callback;
            this.params = param;
            this._url = _url;
        }

        @Override
        protected Object doInBackground(Void... _void) {
            String res = config.getCacher().get(_url);
            if (res != null && config.isUseCache()) { // 如果有缓存
                return res;
            } else {
                DataOutputStream out = null;
                InputStream input = null;
                BufferedReader reader = null;
                StringBuilder respond = null;
                try {
                    URL url = new URL(_url);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setReadTimeout(config.getReadTimeout());
                    conn.setConnectTimeout(config.getConnectTimeOut());
                    conn.setRequestProperty("Charset",
                            config.getCharSet());
                    conn.setRequestProperty("Content-Type",
                            config.getContentType());
                    conn.setInstanceFollowRedirects(true);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);
                    String cookie = config.getCookie();
                    if (!StringUtils.isEmpty(cookie)) {
                        conn.setRequestProperty("Cookie", cookie);
                    }
                    for (Map.Entry<String, String> entry : config
                            .getHeader().entrySet()) {
                        conn.setRequestProperty(entry.getKey(),
                                entry.getValue());
                    }
                    conn.connect();
                    if (params != null) {
                        out = new DataOutputStream(
                                conn.getOutputStream());
                        out.writeBytes(params.toString());
                        out.flush();
                    }

                    input = conn.getInputStream();
                    reader = new BufferedReader(
                            new InputStreamReader(input));
                    respond = new StringBuilder();
                    int i = 0;
                    int current = 0;
                    int count = conn.getContentLength();
                    char[] buf = new char[1024];
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
                    FileUtils.closeIO(out, input, reader);
                }
                return respond;
            }
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
                config.getCacher().add(_url, result.toString());
            }
        }
    }

    /*********************** HttpURLConnection 下载 *************************/

    /**
     * 使用HttpURLConnection方式下载文件，默认启用断点续传
     * 
     * @param url
     *            文件所在URL
     * @param saveFile
     *            文件本地保存路径
     * @param callback
     *            请求中的回调方法，目前只支持类：FileCallBack
     */
    public void urlDownload(String url, File saveFile,
            I_HttpRespond callback) {
        urlDownload(url, saveFile, true, callback);
    }

    /**
     * 使用HttpURLConnection方式下载文件，默认启用断点续传
     * 
     * @param url
     *            文件所在URL
     * @param absFilePath
     *            文件在本地保存的路径
     * @param callback
     *            请求中的回调方法，目前只支持类：FileCallBack
     */
    public void urlDownload(String url, String absFilePath,
            I_HttpRespond callback) {
        urlDownload(url, absFilePath, true, callback);
    }

    /**
     * 使用HttpURLConnection方式下载文件。不使用断点续传的功能还没做，暂时不让外界调用本方法
     * 
     * @param url
     *            文件所在URL
     * @param absFilePath
     *            文件在本地保存的路径
     * @param open
     *            是否开启断点续传
     * @param callback
     *            请求中的回调方法，目前只支持类：FileCallBack
     */
    private void urlDownload(String url, String absFilePath,
            boolean open, I_HttpRespond callback) {
        int s = absFilePath.lastIndexOf(File.separator);
        String dir = null;
        if (s > 0) {
            dir = absFilePath.substring(0, s);
            File file = new File(dir);
            file.mkdirs();
            file = new File(absFilePath);
            try {
                file.createNewFile();
                urlDownload(url, file, open, callback);
            } catch (IOException e) {
                throw new KJException("can not create file");
            }
        } else {
            throw new KJException("can not create file dir");
        }
    }

    /**
     * 使用HttpURLConnection方式下载文件。不使用断点续传的功能还没做，暂时不让外界调用本方法
     * 
     * @param url
     *            文件所在URL
     * @param saveFile
     *            文件本地保存点
     * @param open
     *            是否开启断点续传
     * @param callback
     *            请求中的回调方法，目前只支持类：FileCallBack
     */
    private void urlDownload(String url, File saveFile, boolean open,
            I_HttpRespond callback) {
        if (open) {
            new FileDownloadTask(saveFile, callback).execute(url);
        } else {
        }
    }

    /**
     * 实现HttpUrl下载文件的任务(目前已知BUG，当文件下载过程中中断网络，下载没有停止)
     */
    private class FileDownloadTask extends
            KJTaskExecutor<Object, Object, Object> {
        private File saveFile;
        private I_HttpRespond callback;

        public FileDownloadTask(File saveFile, I_HttpRespond callback) {
            this.saveFile = saveFile;
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object... urls) {
            try {
                // 下载器可以自己通过实现I_FileLoader或者I_MulThreadLoader接口协议
                I_FileLoader result = config.getDownloader();
                if (result == null) {
                    result = new FileDownLoader(urls[0].toString(),
                            saveFile, config.getDownThreadCount());
                }
                result.download(callback);
                return result;
            } catch (KJException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (callback != null) {
                if (result instanceof I_FileLoader) {
                    callback.onSuccess(this.saveFile);
                } else {
                    KJException e = ((KJException) result);
                    callback.onFailure(e, 3721, e.getMessage());
                }
            }
        }
    }

    /**************************** HttpClient method ******************************/

    private DefaultHttpClient httpClient;
    private ThreadPoolExecutor threadPool;
    private HttpContext httpContext;
    private Map<Context, List<WeakReference<Future<?>>>> requestMap;

    /**
     * 初始化httpClient
     */
    private void initHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams,
                config.getConnectTimeOut());
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                new ConnPerRouteBean(config.getMaxConnections()));
        ConnManagerParams.setMaxTotalConnections(httpParams,
                config.getMaxConnections());

        HttpConnectionParams.setSoTimeout(httpParams,
                config.getConnectTimeOut());
        HttpConnectionParams.setConnectionTimeout(httpParams,
                config.getConnectTimeOut());
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams,
                config.getSocketBuffer());

        HttpProtocolParams.setVersion(httpParams,
                HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, "KJLibrary");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                httpParams, schemeRegistry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient
                .addRequestInterceptor(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest request,
                            HttpContext context) {
                        if (!request
                                .containsHeader("Accept-Encoding")) {
                            request.addHeader("Accept-Encoding",
                                    "gzip");
                        }

                        for (Entry<String, String> entry : config
                                .getHeader().entrySet()) {
                            request.addHeader(entry.getKey(),
                                    entry.getValue());
                        }
                    }
                });

        httpClient
                .addResponseInterceptor(new HttpResponseInterceptor() {
                    @Override
                    public void process(HttpResponse response,
                            HttpContext context) {
                        final HttpEntity entity = response
                                .getEntity();
                        if (entity == null) {
                            return;
                        }
                        final Header encoding = entity
                                .getContentEncoding();
                        if (encoding != null) {
                            for (HeaderElement element : encoding
                                    .getElements()) {
                                if (element.getName()
                                        .equalsIgnoreCase("gzip")) {
                                    response.setEntity(new InflatingEntity(
                                            response.getEntity()));
                                    break;
                                }
                            }
                        }
                    }
                });
        threadPool = (ThreadPoolExecutor) KJThreadExecutors
                .newCachedThreadPool();
        httpClient.setHttpRequestRetryHandler(new RetryHandler(config
                .getReadTimeout()));
        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
    }

    /************************* HttpClient config method *************************/

    /**
     * 设置一个可选的cookie去标记请求
     * 
     * @param cookieStore
     *            The CookieStore implementation to use, usually an instance of
     *            PersistentCookieStore
     */
    public void setCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE,
                cookieStore);
    }

    /**
     * 设置代理请求头
     * 
     * @param userAgent
     *            代理请求头
     */
    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(),
                userAgent);
    }

    /**
     * 设置连接超时时间，默认为10s
     * 
     * @param timeout
     */
    public void setTimeout(int timeout) {
        config.setReadTimeout(timeout);
        config.setConnectTimeOut(timeout);
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams
                .setConnectionTimeout(httpParams, timeout);
    }

    /**
     * 设置以https方式连接
     * 
     * @param sslSocketFactory
     */
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.httpClient.getConnectionManager().getSchemeRegistry()
                .register(new Scheme("https", sslSocketFactory, 443));
    }

    /**
     * 添加http请求头
     * 
     * @param header
     * @param value
     */
    public void addHeader(String header, String value) {
        config.getHeader().put(header, value);
    }

    /**
     * Sets basic authentication for the request. Uses AuthScope.ANY. This is
     * the same as setBasicAuth('username','password',AuthScope.ANY)
     * 
     * @param user
     * @param pass
     */
    public void setBasicAuth(String user, String pass) {
        AuthScope scope = AuthScope.ANY;
        setBasicAuth(user, pass, scope);
    }

    /**
     * Sets basic authentication for the request. You should pass in your
     * AuthScope for security. It should be like this
     * setBasicAuth("username","password", new
     * AuthScope("host",port,AuthScope.ANY_REALM))
     * 
     * @param user
     * @param pass
     * @param scope
     *            - an AuthScope object
     * 
     */
    public void setBasicAuth(String user, String pass, AuthScope scope) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                user, pass);
        this.httpClient.getCredentialsProvider().setCredentials(
                scope, credentials);
    }

    /**
     * Cancels any pending (or potentially active) requests associated with the
     * passed Context.
     * <p>
     * <b>Note:</b> This will only affect requests which were created with a
     * non-null android Context. This method is intended to be used in the
     * onDestroy method of your android activities to destroy all requests which
     * are no longer required.
     * 
     * @param context
     *            the android Context instance associated to the request.
     * @param mayInterruptIfRunning
     *            specifies if active requests should be cancelled along with
     *            pending requests.
     */
    public void cancelRequests(Context context,
            boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = requestMap
                .get(context);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(context);
    }

    /************************* HttpClient get请求 *************************/

    public void get(String url, HttpCallBack callback) {
        get(null, url, null, callback);
    }

    public void get(String url, KJStringParams params,
            HttpCallBack callback) {
        get(null, url, params, callback);
    }

    public void get(Context context, String url, HttpCallBack callback) {
        get(context, url, null, callback);
    }

    public void get(Context context, String url,
            KJStringParams params, HttpCallBack callback) {
        if (params != null) {
            StringBuilder str = new StringBuilder(url);
            str.append("?").append(params.toString());
            url = str.toString();
        }
        String res = config.getCacher().get(url);
        if (res != null && callback != null && config.isUseCache()) { // 如果有缓存
            callback.onSuccess(res);
        } else {
            sendRequest(httpClient, httpContext, new HttpGet(url),
                    null, callback, context);
        }
    }

    /************************* HttpClient post请求 *************************/
    public void post(String url, HttpCallBack callback) {
        post(null, url, null, callback);
    }

    public void post(String url, I_HttpParams params,
            HttpCallBack callback) {
        post(null, url, params, callback);
    }

    public void post(Context context, String url,
            I_HttpParams params, HttpCallBack callback) {
        post(context, url, paramsToEntity(params), null, callback);
    }

    public void post(Context context, String url, HttpEntity entity,
            String contentType, HttpCallBack callback) {
        String res = config.getCacher().get(url);
        if (res != null && callback != null && config.isUseCache()) { // 如果有缓存
            callback.onSuccess(res);
        } else {
            sendRequest(
                    httpClient,
                    httpContext,
                    addEntityToRequestBase(new HttpPost(url), entity),
                    contentType, callback, context);
        }
    }

    /************************* HttpClient post请求 *************************/
    public void put(String url, HttpCallBack callback) {
        put(null, url, null, callback);
    }

    public void put(String url, I_HttpParams params,
            HttpCallBack callback) {
        put(null, url, params, callback);
    }

    public void put(Context context, String url, I_HttpParams params,
            HttpCallBack callback) {
        put(context, url, paramsToEntity(params), null, callback);
    }

    public void put(Context context, String url, HttpEntity entity,
            String contentType, HttpCallBack callback) {
        String res = config.getCacher().get(url);
        if (res != null && callback != null && config.isUseCache()) { // 如果有缓存
            callback.onSuccess(res);
        } else {
            sendRequest(httpClient, httpContext,
                    addEntityToRequestBase(new HttpPut(url), entity),
                    contentType, callback, context);
        }
    }

    /************************ httpClient core method *******************************/
    /**
     * 发送一个请求
     * 
     * @param client
     *            httpClient对象
     * @param callback
     *            Http请求过程中的回调方法接口
     * @param context
     */
    protected void sendRequest(DefaultHttpClient client,
            HttpContext httpContext, HttpUriRequest uriRequest,
            String contentType, HttpCallBack callback, Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }
        Future<?> request = threadPool.submit(new AsyncHttpRequest(
                client, httpContext, uriRequest, callback));
        if (context != null) {
            // 在请求集中添加本次请求
            List<WeakReference<Future<?>>> requestList = requestMap
                    .get(context);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(context, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }
    }

    /**
     * 将http参数转换成HttpEntity集合
     * 
     * @param params
     * @return
     */
    private HttpEntity paramsToEntity(I_HttpParams params) {
        HttpEntity entity = null;
        if (params != null) {
            entity = params.getEntity();
        }
        return entity;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(
            HttpEntityEnclosingRequestBase requestBase,
            HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }
        return requestBase;
    }

    /**
     * 对httpClient的请求参数做封装
     * 
     * @author kymjs(kymjs123@gmail.com)
     * @version 1.0
     * @created 2014-8-14
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    /**
     * 一个http请求的线程
     * 
     * @author kymjs(kymjs123@gmail.com)
     */
    private class AsyncHttpRequest implements Runnable {
        private final AbstractHttpClient client;
        private final HttpContext context;
        private final HttpUriRequest request;
        private final HttpCallBack callback;
        private int executionCount;

        public AsyncHttpRequest(AbstractHttpClient client,
                HttpContext context, HttpUriRequest request,
                HttpCallBack callback) {
            this.client = client;
            this.context = context;
            this.request = request;
            this.callback = callback;
        }

        /**
         * 真正去执行一次请求
         */
        private void makeRequest() throws IOException {
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    HttpResponse response = client.execute(request,
                            context);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        if (!Thread.currentThread().isInterrupted()
                                && callback != null) {
                            callback.sendResponseMessage(request
                                    .getRequestLine().getUri(),
                                    config.getCacher(), response);
                        }
                    } else {
                        if (callback != null) {
                            callback.sendFailureMessage(
                                    new IOException("server respond "
                                            + response
                                                    .getStatusLine()
                                                    .getStatusCode()),
                                    "http respond error");
                        }
                    }
                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        throw e;
                    }
                }
            }
        }

        /**
         * 执行一次请求，如果发生错误尝试重连
         */
        private void makeRequestWithRetries() throws ConnectException {
            boolean retry = true;
            IOException cause = null;
            HttpRequestRetryHandler retryHandler = client
                    .getHttpRequestRetryHandler();
            while (retry) {
                try {
                    makeRequest();
                    return;
                } catch (UnknownHostException e) {
                    if (callback != null) {
                        callback.sendFailureMessage(e,
                                "can't resolve host");
                    }
                    return;
                } catch (SocketException e) {
                    if (callback != null) {
                        callback.sendFailureMessage(e,
                                "can't resolve host");
                    }
                    return;
                } catch (SocketTimeoutException e) {
                    if (callback != null) {
                        callback.sendFailureMessage(e,
                                "socket time out");
                    }
                    return;
                } catch (IOException e) {
                    cause = e;
                    retry = retryHandler.retryRequest(cause,
                            ++executionCount, context);
                } catch (NullPointerException e) {
                    cause = new IOException("NPE in HttpClient"
                            + e.getMessage());
                    retry = retryHandler.retryRequest(cause,
                            ++executionCount, context);
                }
            }
            ConnectException ex = new ConnectException();
            ex.initCause(cause);
            throw ex;
        }

        @Override
        public void run() {
            try {
                makeRequestWithRetries();
            } catch (IOException e) {
                if (callback != null) {
                    callback.sendFailureMessage(e, "ConnectException");
                }
            }
        }
    }
}
