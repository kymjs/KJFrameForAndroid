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

/**
 * 一个简化参数版本的SafeTask<br>
 * 
 * <b>优化点：</b>采用并发替代了系统的串行执行,同时修复了2.3之前并行执行大量数据是FC的问题。<br>
 * <b>创建时间</b> 2014-10-24 <br>
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 * 
 * @param <T>
 *            结果返回类型
 */
public abstract class SimpleSafeAsyncTask<T> extends SafeTask<Void, Void, T> {

    @Override
    protected T doInBackgroundSafely(Void... params) throws Exception {
        return doInBackground();
    }

    protected abstract T doInBackground();
}
