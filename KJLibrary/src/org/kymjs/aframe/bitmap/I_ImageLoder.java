/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.bitmap;

import android.graphics.Bitmap;

/**
 * 图片载入接口协议，可自定义实现此协议的下载器
 * 
 * @explain 采用工厂方法模式设计的下载器，本类也是一个抽象工厂类，用于生产byte[]产品
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public interface I_ImageLoder {
    public byte[] loadImage(String imageUrl);

    public Bitmap getBitmapFromDisk(String key);
}
