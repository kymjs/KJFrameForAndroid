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

import org.kymjs.aframe.http.I_HttpRespond;

/**
 * 文件下载器接口协议<br>
 * 
 * <b>创建时间</b> 2014-8-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public interface I_FileLoader {
    /**
     * 开始下载文件，监听下载数量的变化,不显示实时下载进度
     * 
     * @return 已下载文件大小
     */
    int download();

    /**
     * 开始下载文件
     * 
     * @param callback
     *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     */
    int download(I_HttpRespond callback);
}
