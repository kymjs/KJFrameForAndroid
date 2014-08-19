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
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

/**
 * 对于响应失败的请求尝试重连<br>
 * 
 * <b>创建时间</b> 2014-8-14
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class RetryHandler implements HttpRequestRetryHandler {
    /**
     * 重试等待时间。
     */
    private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
    /**
     * 异常白名单，表示网络原因，继续重试
     */
    private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
    /**
     * 异常黑名单，表示用户原因，不重试
     */
    private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

    static {
        // 进行重试，可能是服务器连接掉了
        exceptionWhitelist.add(NoHttpResponseException.class);
        // 进行重试，可能是由于从WI-FI转到3G失败时出现的错误
        exceptionWhitelist.add(UnknownHostException.class);
        // 进行重试，可能是由于从WI-FI转到3G失败时出现的错误
        exceptionWhitelist.add(SocketException.class);

        // 超时则不再重试
        exceptionBlacklist.add(InterruptedIOException.class);
        // SSL协议握手失败则不再重试
        exceptionBlacklist.add(SSLException.class);
    }

    /**
     * 最大超时次数。
     */
    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount,
            HttpContext context) {
        // 是否重试。
        boolean retry = true;

        // 请求是否到达。
        Boolean b = (Boolean) context
                .getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());

        if (executionCount > maxRetries) {
            // 超过最大重试次数则不再重试
            retry = false;
        } else if (isInList(exceptionBlacklist, exception)) {
            // 如果是在黑名单中，则立即取消重试
            retry = false;
        } else if (isInList(exceptionWhitelist, exception)) {
            // 如果是在白名单中，则马上重试
            retry = true;
        } else if (!sent) {
            // 对于其他的错误，只有当请求还没有被完全发送时再重试
            retry = true;
        }

        if (retry) {
            // resend all idempotent requests
            HttpUriRequest currentReq = (HttpUriRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            String requestType = currentReq.getMethod();
            retry = !requestType.equals("POST");
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            exception.printStackTrace();
        }
        return retry;
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable tr) {
        for (Class<?> clazz : list) {
            if (clazz.isInstance(tr)) {
                return true;
            }
        }
        return false;
    }
}
