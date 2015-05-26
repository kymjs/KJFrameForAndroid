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
package org.kymjs.kjframe.bitmap;

import android.graphics.Bitmap;

/**
 * BitmapLibrary中的回调方法
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public abstract class BitmapCallBack {
    /** 载入前回调 */
    public void onPreLoad() {}

    /** bitmap载入完成将回调 */
    public void onSuccess(final Bitmap bitmap) {}

    /** bitmap载入失败将回调 */
    public void onFailure(final Exception e) {}

    /** bitmap载入完成不管成功失败 */
    public void onFinish() {}

    /** bitmap开始加载网络图片 */
    public void onDoHttp() {}
}
