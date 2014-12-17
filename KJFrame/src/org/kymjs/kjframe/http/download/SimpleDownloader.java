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
package org.kymjs.kjframe.http.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
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
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.core.KJAsyncTask;

import android.os.SystemClock;

public class SimpleDownloader extends KJAsyncTask<Object, Object, Object>
        implements I_FileLoader {

    private AbstractHttpClient client;
    private HttpContext context;

    private final FileEntityHandler mFileEntityHandler = new FileEntityHandler();
    private final HttpCallBack callback;
    private final HttpConfig config;

    private int executionCount = 0;
    private String targetUrl = null; // 下载的路径
    private boolean isResume = false; // 是否断点续传

    private long time;

    public SimpleDownloader(HttpConfig config, HttpCallBack callback) {
        this.config = config;
        this.callback = callback;
        initHttpClient();
    }

    /**
     * 初始化httpClient
     */
    private void initHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, config.timeOut);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                new ConnPerRouteBean(config.maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams,
                config.maxConnections);

        HttpConnectionParams.setSoTimeout(httpParams, config.timeOut);
        HttpConnectionParams.setConnectionTimeout(httpParams, config.timeOut);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams,
                config.socketBuffer);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, "KJLibrary");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                httpParams, schemeRegistry);

        context = new SyncBasicHttpContext(new BasicHttpContext());
        client = new DefaultHttpClient(cm, httpParams);
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }

                for (Entry<String, String> entry : config.httpHeader.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new InflatingEntity(response
                                    .getEntity()));
                            break;
                        }
                    }
                }
            }
        });
        client.setHttpRequestRetryHandler(new RetryHandler(config.timeOut));
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (params != null && params.length == 2) {
            targetUrl = String.valueOf(params[0]);
            isResume = (Boolean) params[1];
        }
        try {
            publishProgress(UPDATE_START); // 开始
            makeRequestWithRetries(new HttpGet(targetUrl));
        } catch (IOException e) {
            publishProgress(UPDATE_FAILURE, e, -1, e.getMessage()); // 结束
        }
        return null;
    }

    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    @Override
    protected void onProgressUpdate(Object... values) {
        int update = Integer.valueOf(String.valueOf(values[0]));
        switch (update) {
        case UPDATE_START:
            if (callback != null) {
                callback.onPreStart();
            }
            break;
        case UPDATE_LOADING:
            if (callback != null && SystemClock.uptimeMillis() - time >= 1000) {
                time = SystemClock.uptimeMillis();
                callback.onLoading(Long.valueOf(String.valueOf(values[1])),
                        Long.valueOf(String.valueOf(values[2])));
            }
            break;
        case UPDATE_FAILURE:
            if (callback != null) {
                callback.onFailure((Throwable) values[1], (Integer) values[2],
                        (String) values[3]);
            }
            break;
        case UPDATE_SUCCESS:
            if (callback != null) {
                callback.onSuccess((File) values[1]);
            }
            break;
        }
        super.onProgressUpdate(values);
    }

    /**
     * 执行网络请求
     * 
     * @param request
     * @throws IOException
     */
    private void makeRequestWithRetries(HttpUriRequest request)
            throws IOException {
        if (isResume && targetUrl != null) {
            File downloadFile = new File(targetUrl);
            long fileLen = 0;
            if (downloadFile.isFile() && downloadFile.exists()) {
                fileLen = downloadFile.length();
            }
            if (fileLen > 0) {
                request.setHeader("RANGE", "bytes=" + fileLen + "-");
            }
        }

        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client
                .getHttpRequestRetryHandler();
        while (retry) {
            try {
                if (!isCancelled()) {
                    HttpResponse response = client.execute(request, context);
                    if (!isCancelled()) {
                        handleResponse(response);
                    }
                }
                return;
            } catch (UnknownHostException e) {
                publishProgress(UPDATE_FAILURE, e, 0,
                        "unknownHostException：can't resolve host");
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount,
                        context);
            } catch (NullPointerException e) {
                // HttpClient 4.0.x 之前的一个bug
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount,
                        context);
            } catch (Exception e) {
                cause = new IOException("Exception" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount,
                        context);
            }
        }
        if (cause != null) {
            throw cause;
        } else {
            throw new IOException("未知网络错误");
        }
    }

    private void handleResponse(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            String errorMsg = "response status error code:"
                    + status.getStatusCode();
            if (status.getStatusCode() == 416 && isResume) {
                errorMsg += " \n maybe you have download complete.";
            }
            publishProgress(
                    UPDATE_FAILURE,
                    new HttpResponseException(status.getStatusCode(), status
                            .getReasonPhrase()), status.getStatusCode(),
                    errorMsg);
        } else {
            try {
                HttpEntity entity = response.getEntity();
                Object responseBody = null;
                if (entity != null) {
                    time = SystemClock.uptimeMillis();
                    if (targetUrl != null) {
                        responseBody = mFileEntityHandler.handleEntity(entity,
                                getDownloadProgressListener(), config.savePath,
                                isResume);
                    }
                }
                publishProgress(UPDATE_SUCCESS, responseBody);
            } catch (Exception e) {
                publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
            }

        }
    }

    private DownloadProgress getDownloadProgressListener() {
        return new DownloadProgress() {
            @Override
            public void onProgress(long count, long current) {
                publishProgress(UPDATE_LOADING, count, current);
            }
        };
    }

    public interface DownloadProgress {
        void onProgress(long count, long current);
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

    @Override
    public void doDownload(String url, boolean isResume) {
        execute(url, isResume);
    }

    @Override
    public boolean isStop() {
        return mFileEntityHandler.isStop();
    }

    @Override
    public void stop() {
        mFileEntityHandler.setStop(true);
    }
}
