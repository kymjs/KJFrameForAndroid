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
package org.kymjs.kjframe.http;

import android.graphics.Bitmap;

import java.util.Map;

/**
 * Http请求回调类<br>
 * 
 * <b>创建时间</b> 2014-8-7
 *
 * @author kymjs (http://www.kymjs.com/) .
 * @version 1.4
 */
public abstract class HttpCallBack {

    /**
     * 请求开始之前回调
     */
    public void onPreStart() {
    }

    /**
     * 注意：本方法将在异步调用。
     * Http异步请求成功时在异步回调,并且仅当本方法执行完成才会继续调用onSuccess()
     *
     * @param t 返回的信息
     */
    public void onSuccessInAsync(byte[] t) {
    }

    /**
     * Http请求成功时回调
     * 
     * @param t
     *            HttpRequest返回信息
     */
    public void onSuccess(String t) {}

    /**
     * Http请求成功时回调
     * 
     * @param t
     *            HttpRequest返回信息
     */
    public void onSuccess(byte[] t) {
        if (t != null) {
            onSuccess(new String(t));
        }
    }

    /**
     * Http请求成功时回调
     * 
     * @param headers
     *            HttpRespond头
     * @param t
     *            HttpRequest返回信息
     */
    public void onSuccess(Map<String, String> headers, byte[] t) {
        onSuccess(t);
    }

    /**
     * 仅在KJBitmap中可用，图片加载完成时回调
     * 
     * @param t
     */
    public void onSuccess(Bitmap t) {}

    /**
     * Http请求失败时回调
     * 
     * @param errorNo
     *            错误码
     * @param strMsg
     *            错误原因
     */
    public void onFailure(int errorNo, String strMsg) {}

    /**
     * Http请求结束后回调
     */
    public void onFinish() {}

    /**
     * 进度回调，仅支持Download时使用
     * 
     * @param count
     *            总数
     * @param current
     *            当前进度
     */
    public void onLoading(long count, long current) {}
}
