package org.kymjs.aframe.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.kymjs.aframe.ThreadPoolManager;

/**
 * 网络请求用户接口类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-5
 */
public class KJFrameHttp {
    
    private ThreadPoolManager pool;
    private HttpConfig config;
    
    public KJFrameHttp(HttpConfig config) {
        this.config = config;
        pool = ThreadPoolManager.create();
    }
    
    public KJFrameHttp() {
        this(new HttpConfig());
    }
    
    /****************************** http请求 **********************************/
    
    public void urlGet(String url, KJParams params,
            HttpCallBack<? extends Object> callback) {
        if (params != null) {
            StringBuilder str = new StringBuilder(url);
            str.append("?").append(params.toString());
            url = str.toString();
        }
        urlGet(url, callback);
    }
    
    public void urlGet(String url, HttpCallBack<? extends Object> callback) {
        pool.addTask(getGetHttpThread(url, callback));
    }
    
    public void urlPost(String url, KJParams params,
            HttpCallBack<? extends Object> callback) {
        pool.addTask(getPostHttpThread(url, params, callback));
    }
    
    /**
     * 获取一个Post请求线程
     * 
     * @param _url
     *            网络地址
     * @param params
     *            post附带的参数集
     * @param callback
     *            回调接口
     */
    private Runnable getPostHttpThread(final String _url,
            final KJParams params, final HttpCallBack<? extends Object> callback) {
        return new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                OutputStream output = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(_url);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(config.isUseCache());
                    conn.setReadTimeout(config.getReadTimeout());
                    conn.setConnectTimeout(config.getConnectTimeOut());
                    conn.setRequestProperty("Charset", config.getCharSet());
                    output = conn.getOutputStream();
                    input = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    output.write(params.toString().getBytes());
                    StringBuilder respond = new StringBuilder();
                    int i = 0;
                    char[] buf = new char[1024];
                    while ((i = reader.read(buf)) != -1) {
                        respond.append(buf, 0, i);
                    }
                    callback.onSuccess(respond.toString());
                } catch (MalformedURLException e) {
                    callback.onFailure(e, 3721, "URL错误");
                } catch (IOException e) {
                    callback.onFailure(e, 3722, "IO错误");
                } finally {
                    if (input != null)
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (output != null)
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (reader != null)
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        };
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
            final HttpCallBack<? extends Object> callback) {
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
                    char[] buf = new char[1024];
                    while ((i = reader.read(buf)) != -1) {
                        respond.append(buf, 0, i);
                    }
                    callback.onSuccess(respond.toString());
                } catch (MalformedURLException e) {
                    callback.onFailure(e, 3721, "URL错误");
                } catch (IOException e) {
                    callback.onFailure(e, 3722, "IO错误");
                } finally {
                    if (input != null)
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (reader != null)
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        };
    }
}
