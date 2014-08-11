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
package org.kymjs.aframe.http.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.kymjs.aframe.KJException;
import org.kymjs.aframe.http.I_HttpRespond;

/**
 * 多线程文件下载器类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-8-11
 */
public class FileDownLoader implements I_MulThreadLoader {
    private FragmentFile fragmentFile; // 多线程下载的碎片块文件
    private int loadSize = 0; // 已下载文件长度
    private int fileSize = 0; // 原始文件长度
    private DownloadThread[] threads; // 多线程
    private File saveFile; // 本地保存文件
    private int block; // 每条线程下载的长度
    private String loadUrl; // 下载路径
    // 缓存各线程下载的长度(k作为线程id,v作为下载的长度)
    SparseIntArray data = new SparseIntArray();

    /**
     * 构建文件下载器,并没有流操作，不算耗时操作
     * 
     * @param _url
     *            下载路径
     * @param absFilePath
     *            文件保存点
     * @param threadNum
     *            下载线程数
     */
    public FileDownLoader(String _url, File saveFile, int threadNum) {
        this.loadUrl = _url;
        fragmentFile = new FragmentFile();
        this.threads = new DownloadThread[threadNum];

        if (saveFile.isDirectory()) {
            throw new KJException("absFilePath is directory");
        }
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                throw new KJException("absFilePath can not create");
            }
        }
        this.saveFile = saveFile; // 构建保存文件

        try {
            URL url = new URL(this.loadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Referer", _url);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                // 根据响应获取文件大小
                this.fileSize = conn.getContentLength();
                if (this.fileSize <= 0) {
                    throw new KJException("Unkown file size ");
                }
                SparseIntArray logdata = fragmentFile.getData(_url);// 获取下载记录
                if (logdata.size() > 0) { // 如果存在下载记录
                    for (int i = 0; i < logdata.size(); i++) {
                        // 把各条线程已经下载的数据长度放入data中
                        data.put(logdata.keyAt(i), logdata.valueAt(i));
                    }
                }

                if (this.data.size() == this.threads.length) {
                    // 下面计算所有线程已经下载的数据长度
                    for (int i = 0; i < this.threads.length; i++) {
                        this.loadSize += this.data.get(i + 1);
                    }
                }

                // 计算每条线程应该下载数据长度
                this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize
                        / this.threads.length
                        : this.fileSize / this.threads.length + 1;
            } else {
                throw new KJException("server response code is: "
                        + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            throw new KJException("don't connection this url");
        } catch (IOException e) {
            throw new KJException("connection error");
        }
    }

    /**
     * 开始下载文件
     * 
     * @param listener
     *            监听下载数量的变化,不显示实时下载进度
     * @return 已下载文件大小
     */
    @Override
    public int download() {
        return download(null);
    }

    /**
     * 开始下载文件
     * 
     * @param listener
     *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     */
    @Override
    public int download(I_HttpRespond callback) {
        URL url = null;
        try {
            RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
            if (this.fileSize > 0) {
                randOut.setLength(this.fileSize);
            }
            randOut.close();
            url = new URL(this.loadUrl);
        } catch (MalformedURLException e) {
            if (callback != null) {
                callback.onFailure(e, 3721, "file download fail :url error");
            }
            throw new KJException("file download fail :url error");
        } catch (FileNotFoundException e) {
            if (callback != null) {
                callback.onFailure(e, 3721,
                        "file download fail :saveFile not found");
            }
            throw new KJException("file download fail :saveFile not found");
        } catch (IOException e) {
            if (callback != null) {
                callback.onFailure(e, 3721,
                        "file download fail :fileclose error,IOException");
            }
            throw new KJException(
                    "file download fail :fileclose error,IOException");
        }

        // 如果map集合中保存的线程数与程序的线程数不同，则重新初始化
        if (this.data.size() != this.threads.length) {
            this.data.clear();
            for (int i = 0; i < this.threads.length; i++) {
                // 初始化每条线程已经下载的数据长度为0
                this.data.put(i + 1, 0);
            }
        }

        // 开启线程进行下载
        for (int i = 0; i < this.threads.length; i++) {
            int downLength = this.data.get(i + 1);
            // 判断线程是否已经完成下载,否则继续下载
            if (downLength < this.block && this.loadSize < this.fileSize) {
                this.threads[i] = new DownloadThread(this, url, this.saveFile,
                        this.block, this.data.get(i + 1), i + 1);
                this.threads[i].setPriority(7);
                this.threads[i].start();
            } else {
                this.threads[i] = null;
            }
        }
        this.fragmentFile.save(this.loadUrl, this.data);
        boolean notFinish = true;// 下载未完成

        while (notFinish) {// 循环判断所有线程是否完成下载
            // 如果设置了进度监听器，则回调相应方法
            if (callback != null) {
                callback.onLoading(this.fileSize, this.loadSize);
            }
            notFinish = false;// 假定全部线程下载完成

            for (int i = 0; i < this.threads.length; i++) {
                // 如果发现线程未完成下载
                if (this.threads[i] != null && !this.threads[i].isFinish()) {
                    notFinish = true;// 设置标志为下载没有完成
                    // 如果下载失败,再重新下载
                    if (this.threads[i].getDownLength() == -1) {
                        this.threads[i] = new DownloadThread(this, url,
                                this.saveFile, this.block,
                                this.data.get(i + 1), i + 1);
                        this.threads[i].start();
                    }
                }
            }
        }
        if (callback != null) {
            callback.onSuccess(this.saveFile);
        }
        fragmentFile.delete(this.loadUrl);
        return this.loadSize;
    }

    /**
     * 获取文件大小
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 累计已下载大小
     * 
     * @param size
     */
    @Override
    public synchronized void append(int size) {
        loadSize += size;
    }

    /**
     * 更新指定线程最后下载的位置
     * 
     * @param threadId
     *            线程id
     * @param pos
     *            最后下载的位置
     */
    @Override
    public synchronized void update(int threadId, int pos) {
        this.data.put(threadId, pos);
        this.fragmentFile.update(this.loadUrl, this.data);
    }
}
