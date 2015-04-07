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
package org.kymjs.kjframe.widget;

/**
 * 包含刷新和加载更多地接口方法
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public interface KJRefreshListener {
    /**
     * 下拉刷新回调接口
     */
    public void onRefresh();

    /**
     * 上拉刷新回调接口
     */
    public void onLoadMore();
}