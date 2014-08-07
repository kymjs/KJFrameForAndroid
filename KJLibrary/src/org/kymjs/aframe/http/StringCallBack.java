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
 * 用于处理JSON的http请求回调类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-8-5
 */
public abstract class StringCallBack implements I_HttpRespond {
    private boolean progress = false;

    public boolean isProgress() {
        return progress;
    }

    public void setProgress(boolean open) {
        progress = open;
    }

    abstract public void onSuccess(String json);

    @Override
    public void onLoading(long count, long current) {}

    @Override
    public void onFailure(Throwable t, int errorNo, String strMsg) {}

    @Override
    public void onSuccess(Object t) {
        onSuccess(t.toString());
    }
}
