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

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 在完成一组正在其他线程中执行的操作之前, 它允许一个或多个线程一直等待
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class BarrierExecutor {

    private final ArrayList<KJAsyncTask<?, ?, ?>> taskList = new ArrayList<KJAsyncTask<?, ?, ?>>();
    private transient boolean isRunning = false;

    /**
     * 添加一个待执行的异步任务
     * 
     * @param task
     * @return
     */
    public BarrierExecutor put(KJAsyncTask task) {
        if (task != null) {
            taskList.add(task);
        }
        return this;
    }

    /**
     * 开始执行异步任务，且直到所有异步任务执行完成才开始参数任务执行
     * 
     * @param finishOnUiTask
     *            关卡任务，在UI线程中运行
     */
    @SuppressWarnings("unchecked")
    public void start(final KJAsyncTask finishOnUiTask) {
        if (isRunning) {
            throw new RuntimeException(
                    "CyclicBarrierExecutor only can start once.");
        }
        isRunning = true;
        // 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待
        final CountDownLatch latch = new CountDownLatch(taskList.size());
        new SimpleAsyncTask<Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void _void) {
                finishOnUiTask.execute();
            }
        }.execute();
        startInternal(latch);
    }

    /**
     * 开始执行异步任务，且直到所有异步任务执行完成才开始参数线程执行
     * 
     * @param endOnUiThread
     *            关卡任务，在UI线程中运行
     */
    public void start(final Runnable endOnUiThread) {
        if (isRunning) {
            throw new RuntimeException(
                    "CyclicBarrierExecutor only can start once.");
        }
        isRunning = true;

        // 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待
        final CountDownLatch latch = new CountDownLatch(taskList.size());
        new SimpleAsyncTask<Void>() {

            @Override
            protected Void doInBackground() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void _void) {
                endOnUiThread.run();
            }
        }.execute();
        startInternal(latch);
    }

    /**
     * 真正开始执行关卡任务
     * 
     * @param latch
     */
    @SuppressWarnings("unchecked")
    private void startInternal(final CountDownLatch latch) {
        for (KJAsyncTask<?, ?, ?> each : taskList) {
            each.setOnFinishedListener(new KJAsyncTask.OnFinishedListener() {
                @Override
                public void onPostExecute() {
                    latch.countDown();
                }

                @Override
                public void onCancelled() {
                    latch.countDown();
                }
            });
            each.execute();
        }
    }// end method
}
