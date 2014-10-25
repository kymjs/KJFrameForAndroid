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
package org.kymjs.aframe.http.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;

/**
 * the {@link TaskExecutor} can execute task in many ways.
 * <ul>
 * <li>1. CyclicBarrierTask, 并发的执行一系列任务，且会在所有任务执行完成时集中到一个关卡点（执行特定的函数）。
 * <li>2. Delayed Task, 延时任务。
 * <li>3. Timer Runnable, 定时任务。
 * </ul>
 * 
 * @author MaTianyu 2014-2-3下午6:30:14
 */
public class TaskExecutor {

    /**
     * 开子线程，并发超出数量限制时允许丢失任务。
     * 
     * @param run
     */
    public static void start(Runnable run) {
        KJTaskExecutor.execute(run);
    }

    /**
     * 延时异步任务
     * 
     * @param task
     * @param time
     *            延迟时间：秒
     */
    public static void startDelayedTask(
            final KJTaskExecutor<?, ?, ?> task, long time) {
        long delay = time;
        delay = TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS);
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void run() {
                        task.execute();
                    }
                }, delay);
    }

    /**
     * 启动定时任务,具体参照TimerTask类scheduleAtFixedRate()
     * 
     * @param run
     * @param delay
     *            >0 延迟时间
     * @param period
     *            >0 心跳间隔时间
     * @return
     */
    public static Timer startTimerTask(final Runnable run,
            long delay, long period) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        };
        timer.scheduleAtFixedRate(timerTask, delay, period);
        return timer;
    }
}
