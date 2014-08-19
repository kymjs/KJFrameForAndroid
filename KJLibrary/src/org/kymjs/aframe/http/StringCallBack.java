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
 * 用于处理JSON的http请求回调类<br>
 * 
 * <b>创建时间</b> 2014-8-5
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 */
public abstract class StringCallBack extends HttpCallBack {

    /**
     * 网络请求成功后回调
     */
    abstract public void onSuccess(String json);

    /**
     * 上传进度回调，必须调用了setProgress(true)，该方法才会回调
     */
    @Override
    public void onLoading(long count, long current) {}

    /**
     * 网络请求异常后回调
     */
    @Override
    public void onFailure(Throwable t, int errorNo, String strMsg) {}

    @Override
    public void onSuccess(Object t) {
        onSuccess(t.toString());
    }
}
