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

/**
 * 多线程文件下载器接口协议<br>
 * 
 * <b>创建时间</b> 2014-8-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public interface I_MulThreadLoader extends I_FileLoader {
    /**
     * 累计已下载大小
     * 
     * @param size
     */
    void append(int size);

    /**
     * 更新指定线程最后下载的位置
     * 
     * @param threadId
     *            线程id
     * @param pos
     *            最后下载的位置
     */
    void update(int threadId, int pos);
}
