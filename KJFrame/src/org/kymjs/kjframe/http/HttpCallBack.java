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

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Http请求回调类<br>
 * 
 * <b>创建时间</b> 2014-8-7
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.3
 */
public abstract class HttpCallBack {
    public int respondCode = -1;

    /**
     * Http请求开始前回调
     */
    public void onPreStart() {};

    /**
     * Http请求连接时调用
     * 
     * <b>waring</b> run in asynchrony thread
     */
    public void onHttpConnection(HttpURLConnection conn) {}

    /**
     * 进度回调，仅支持Download时使用
     * 
     * @param count
     *            总数
     * @param current
     *            当前进度
     */
    public void onLoading(long count, long current) {}

    /**
     * Http请求成功时回调
     * 
     * @param t
     */
    public void onSuccess(String t) {}
    
    /**
     * Http请求成功时回调
     * 
     * @param t
     */
    public void onSuccessFromCache(int code, String t) {
    	onSuccess(code, t);
    }

    /**
     * Http请求成功时回调
     * 
     * @param code
     *            请求码
     * @param t
     *            Http请求返回信息
     */
    public void onSuccess(int code, String t) {
        onSuccess(t);
    }

    /**
     * Http下载成功时回调
     */
    public void onSuccess(File f) {}

    /**
     * Http请求失败时回调
     * 
     * @param t
     * @param errorNo
     *            错误码
     * @param strMsg
     *            错误原因
     */
    public void onFailure(Throwable t, int errorNo, String strMsg) {}

    /**
     * Http请求结束后回调
     */
    public void onFinish() {}
}
