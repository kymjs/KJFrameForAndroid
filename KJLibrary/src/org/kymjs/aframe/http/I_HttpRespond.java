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

/**
 * http请求规范接口协议
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-8-7
 */
public interface I_HttpRespond {
    /**
     * 开发者必须实现该方法，供外部调用（启动进度回调）
     */
    boolean isProgress();

    /**
     * 开发者必须实现该方法，供外部调用（启动进度回调）
     */
    void setProgress(boolean open);

    /**
     * 进度回调，一秒回调一次
     * 
     * @param count
     *            总数
     * @param current
     *            当前进度
     */
    void onLoading(long count, long current);

    /**
     * http请求成功时回调
     * 
     * @param t
     */
    void onSuccess(Object t);

    /**
     * http请求失败时回调
     * 
     * @param t
     * @param errorNo
     *            错误码
     * @param strMsg
     *            错误原因
     */
    void onFailure(Throwable t, int errorNo, String strMsg);
}
