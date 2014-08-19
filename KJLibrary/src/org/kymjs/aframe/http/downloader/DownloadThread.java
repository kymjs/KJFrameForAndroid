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
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.aframe.KJLoger;

/**
 * 多线程下载中实现每个线程的下载任务的类<br>
 * 
 * <b>创建时间</b> 2014-8-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class DownloadThread extends Thread {
    private File saveFile; // 保存的文件
    private URL url; // 下载地址
    private int block; // 下载的大小
    private int threadId = -1; // 当前线程的ID
    private int downLength; // 已经下载的长度
    private I_MulThreadLoader downloader; // 调用本线程的下载器类

    private boolean finish = false; // 是否已经下载完成

    /**
     * 构造方法
     * 
     * @param downloader
     *            下载器
     * @param url
     *            下载地址
     * @param saveFile
     *            下载路径
     * @param block
     *            要下载的文件大小
     * @param downLength
     *            已经下载的文件大小
     * @param threadId
     *            当前线程id
     */
    public DownloadThread(I_MulThreadLoader downloader, URL url, File saveFile,
            int block, int downLength, int threadId) {
        this.url = url;
        this.saveFile = saveFile;
        this.block = block;
        this.downloader = downloader;
        this.threadId = threadId;
        this.downLength = downLength;
    }

    @Override
    public void run() {
        if (downLength < block) {// 未下载完成
            try {
                // 使用Get方式下载
                HttpURLConnection http = (HttpURLConnection) url
                        .openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setReadTimeout(5 * 1000);
                http.setRequestMethod("GET");
                http.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                http.setRequestProperty("Accept-Language", "zh-CN");
                http.setRequestProperty("Referer", url.toString());
                http.setRequestProperty("Charset", "UTF-8");

                int startPos = block * (threadId - 1) + downLength;// 开始位置
                int endPos = block * threadId - 1;// 结束位置
                http.setRequestProperty("Range", "bytes=" + startPos + "-"
                        + endPos);// 设置获取实体数据的范围
                http.setRequestProperty("Connection", "Keep-Alive");

                InputStream inStream = http.getInputStream();
                byte[] buffer = new byte[1024];
                int offset = 0;
                RandomAccessFile threadfile = new RandomAccessFile(
                        this.saveFile, "rwd");
                threadfile.seek(startPos);

                while ((offset = inStream.read(buffer, 0, 1024)) != -1) {
                    threadfile.write(buffer, 0, offset);
                    downLength += offset;
                    downloader.update(this.threadId, downLength);
                    downloader.append(offset);
                }
                threadfile.close();
                inStream.close();
                this.finish = true;
            } catch (Exception e) {
                this.downLength = -1;
                KJLoger.debug("Thread " + this.threadId + ":" + e);
            }
        }
    }

    /**
     * 下载是否完成
     * 
     * @return
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * 已经下载的内容大小
     * 
     * @return 如果返回值为-1,代表下载失败
     */
    public long getDownLength() {
        return downLength;
    }
}
