/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-5
 */
final public class ThreadPoolManager {
    /*
     * 单类模式
     */
    private static ThreadPoolManager instance = null;
    private static ExecutorService service = null;

    private ThreadPoolManager(int size) {
        if (size < 0) {
            size = Runtime.getRuntime().availableProcessors() * 2;
        }
        service = Executors.newFixedThreadPool(size);
    }

    /**
     * 创建线程池大小
     * 
     * @param size
     *            线程池大小
     */
    public synchronized static ThreadPoolManager create(int size) {
        if (instance == null) {
            instance = new ThreadPoolManager(size);
        }
        return instance;
    }

    /**
     * 默认创建系统处理器个数*2个线程
     */
    public static ThreadPoolManager create() {
        return create(-1);
    }

    /**
     * 在线程池中执行我传进来的任务
     */
    public void addTask(Runnable run) {
        service.execute(run);
    }
}
