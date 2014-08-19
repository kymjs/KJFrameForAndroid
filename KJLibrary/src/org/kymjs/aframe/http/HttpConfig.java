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

import org.kymjs.aframe.http.downloader.I_FileLoader;

/**
 * HttpClient请求的配置类<br>
 * 
 * <b>创建时间</b> 2014-6-5
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 */
public class HttpConfig {
    public static final String FileParamsName = "KJFrameForAndroid_File";
    public static final String FileParamsKey = "KJLibrary";

    private static final int THREAD_COUNT = 6;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB
    private static final int SOCKET_TIMEOUT = 8 * 1000; // 8秒
    private static final int READ_TIMEOUT = 8 * 1000; // 8秒
    private static final String CHAR_SET = "UTF8";
    private static final boolean DO_OUT_PUT = true;
    private static int MAX_CONNECTION = 10;
    private static final String TYPE = "application/x-www-form-urlencoded";

    private int socketBuffer; // socket缓冲区大小
    private int connectTimeOut; // 连接主机超时时间
    private int readTimeout; // 从主机读取数据超时时间
    private String charSet; // 字符编码格式
    private boolean doOutput; // 是否输出
    private boolean doInput; // 是否输入
    private boolean followRedirects; // 是否自动执行HTTP重定向
    private boolean useCache;
    private String contentType;
    private int maxConnections; // http请求最大并发连接数
    private int downThreadCount;
    private I_FileLoader downloader; // 文件下载器

    public HttpConfig() {
        socketBuffer = DEFAULT_SOCKET_BUFFER_SIZE;
        connectTimeOut = SOCKET_TIMEOUT;
        readTimeout = READ_TIMEOUT;
        charSet = CHAR_SET;
        doOutput = DO_OUT_PUT;
        useCache = false;
        contentType = TYPE;
        maxConnections = MAX_CONNECTION;
        downThreadCount = THREAD_COUNT;
    }

    /**
     * 是否启用Cache
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * 是否启用Cache
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * socket缓冲
     */
    public int getSocketBuffer() {
        return socketBuffer;
    }

    /**
     * socket缓冲
     */
    public void setSocketBuffer(int socketBuffer) {
        this.socketBuffer = socketBuffer;
    }

    /**
     * 链接响应超时时间
     */
    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    /**
     * 链接响应超时时间
     */
    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    /**
     * 链接超时时间
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 链接超时时间
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 字符串格式（默认UTF-8）
     */
    public String getCharSet() {
        return charSet;
    }

    /**
     * 字符串格式（默认UTF-8）
     */
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public boolean isDoOutput() {
        return doOutput;
    }

    public void setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
    }

    public boolean isDoInput() {
        return doInput;
    }

    public void setDoInput(boolean doInput) {
        this.doInput = doInput;
    }

    /**
     * 自动执行HTTP重定向
     */
    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * 自动执行HTTP重定向
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * http请求最大并发连接数
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * http请求最大并发连接数
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 多线程下载的线程数
     */
    public int getDownThreadCount() {
        return downThreadCount;
    }

    /**
     * 多线程下载的线程数
     */
    public void setDownThreadCount(int downThreadCount) {
        this.downThreadCount = downThreadCount;
    }

    /**
     * 文件下载器，默认采用系统自带下载器，你也可以使用自己的下载器
     */
    public I_FileLoader getDownloader() {
        return downloader;
    }

    /**
     * 设置文件下载器，默认采用系统自带下载器，你也可以使用自己的下载器
     * 
     * @param downloader
     */
    public void setDownloader(I_FileLoader downloader) {
        this.downloader = downloader;
    }

}
