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

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.kymjs.aframe.http.cache.I_HttpCache;
import org.kymjs.aframe.utils.StringUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 用于HttpClient请求时线程通信的处理、如果使用HttpUrlConnection无需继承该类<br>
 * 
 * <b>创建时间</b> 2014-8-14
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public abstract class HttpCallBack implements I_HttpRespond {
    /************************** class method ***********************************/

    protected Handler handler;
    protected static final int MESSAGE_SUCCESS = 0;
    protected static final int MESSAGE_FAILURE = 1;

    public HttpCallBack() {
        // 该handler用于发送事件到当前线程
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    HttpCallBack.this.handleMessage(msg);
                }
            };
        }
    }

    protected void handleMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
        case MESSAGE_SUCCESS:
            response = (Object[]) msg.obj;
            handleSuccessMessage((Header[]) response[1],
                    (String) response[2]);
            break;
        case MESSAGE_FAILURE:
            response = (Object[]) msg.obj;
            handleFailureMessage((Throwable) response[0],
                    StringUtils.toInt(response[2]),
                    (String) response[1]);
            break;
        default:
        }
    }

    protected void handleSuccessMessage(Header[] headers,
            String responseBody) {
        onSuccess(responseBody);
    }

    protected void handleFailureMessage(Throwable e, int statusCode,
            String responseBody) {
        onFailure(e, statusCode, responseBody);
    }

    // 在后台线程池中的线程中执行
    protected void sendSuccessMessage(int status, Header[] headers,
            String responseBody) {
        sendMessage(obtainMessage(MESSAGE_SUCCESS, new Object[] {
                Integer.valueOf(status), headers, responseBody }));
    }

    protected void sendFailureMessage(int status, Throwable e) {
        sendFailureMessage(e, e.getMessage(), status);
    }

    protected void sendFailureMessage(Throwable e, String responseBody) {
        sendFailureMessage(e, responseBody, 203);
    }

    protected void sendFailureMessage(Throwable e,
            String responseBody, int status) {
        sendMessage(obtainMessage(MESSAGE_FAILURE, new Object[] { e,
                responseBody, status }));
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage,
            Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage,
                    response);
        } else {
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

    // 异步HTTP请求的接口。
    void sendResponseMessage(String uri, I_HttpCache cacher,
            HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            sendFailureMessage(e, (String) null);
        }
        if (status.getStatusCode() >= 300) {
            sendFailureMessage(
                    new HttpResponseException(status.getStatusCode(),
                            status.getReasonPhrase()), responseBody);
        } else {
            cacher.add(uri, responseBody);
            sendSuccessMessage(status.getStatusCode(),
                    response.getAllHeaders(), responseBody);
        }
    }

    /************************** class method ***********************************/
    protected boolean progress = false;

    @Override
    public boolean isProgress() {
        return progress;
    }

    @Override
    public void setProgress(boolean open) {
        this.progress = open;
    }
}
