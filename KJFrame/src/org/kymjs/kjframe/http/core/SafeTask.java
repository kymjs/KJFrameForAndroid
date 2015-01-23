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
package org.kymjs.kjframe.http.core;

import org.kymjs.kjframe.utils.KJLoger;

import android.os.AsyncTask;

/**
 * 安全异步任务，可以捕获任意异常，并反馈给给开发者。<br>
 * 从执行前，执行中，执行后，乃至更新时的异常都捕获。<br>
 * 当{@link #doInBackgroundSafely(Object...)}有异常时，Exception将会被传递到
 * {@link #onPostExecuteSafely(Object, Exception)}。 <br>
 * 如果用户取消了任务，那么会将回调org.kymjs.kjframe.http.HttpCallback.onCancelled().
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public abstract class SafeTask<Params, Progress, Result> extends
        KJAsyncTask<Params, Progress, Result> {
    private Exception cause;

    @Override
    protected final void onPreExecute() {
        try {
            onPreExecuteSafely();
        } catch (Exception e) {
            KJLoger.exception(e);
        }
    }

    @Override
    protected final Result doInBackground(Params... params) {
        try {
            return doInBackgroundSafely(params);
        } catch (Exception e) {
            KJLoger.exception(e);
            cause = e;
        }
        return null;
    }

    @Override
    protected final void onProgressUpdate(Progress... values) {
        try {
            onProgressUpdateSafely(values);
        } catch (Exception e) {
            KJLoger.exception(e);
        }
    }

    @Override
    protected final void onPostExecute(Result result) {
        try {
            onPostExecuteSafely(result, cause);
        } catch (Exception e) {
            KJLoger.exception(e);
        }
    }

    @Override
    protected final void onCancelled(Result result) {
        super.onCancelled(result);
    }

    /**
     * 取代了{@link AsyncTask#onPreExecute()}, 这个方法的任意异常都能被捕获：它是安全的。<br>
     * 注意：本方法将在开发者启动任务的线程执行。
     */
    protected void onPreExecuteSafely() throws Exception {}

    /**
     * Child Thread<br>
     * 取代了{@link AsyncTask#doInBackground(Object...)}, 这个方法的任意异常都能被捕获：它是安全的。
     * 如果它发生了异常，Exception将会通过{@link #onPostExecuteSafely(Object, Exception)} 传递。
     * 
     * @param params
     *            入参
     * @return
     */
    protected abstract Result doInBackgroundSafely(Params... params)
            throws Exception;

    /**
     * Main UI Thread<br>
     * 用于取代{@link AsyncTask#onPostExecute(Object)}。<br>
     * 注意：本方法一定执行在主线程, 这个方法的任意异常都能被捕获：它是安全的。
     * 
     * @param result
     */
    protected void onPostExecuteSafely(Result result, Exception e)
            throws Exception {}

    /**
     * Main UI Thread<br>
     * 用于取代{@link AsyncTask#onProgressUpdate(Object...)},<br>
     * 这个方法的任意异常都能被捕获：它是安全的。<br>
     * 
     * @param values
     *            更新传递的值
     */
    protected void onProgressUpdateSafely(Progress... values) throws Exception {}
}
