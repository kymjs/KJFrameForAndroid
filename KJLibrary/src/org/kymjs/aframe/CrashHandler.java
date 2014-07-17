/*
 * Copyright (c) 2014-2015, kymjs 张涛 (kymjs123@gmail.com).
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import org.kymjs.aframe.ui.KJActivityManager;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;
import android.os.Looper;

/**
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
 * 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * @author wangjiegulu
 * @alter kymjs(kymjs123@gmail.com)
 * @from https://github.com/wangjiegulu/AndroidBucket.git
 * @warn 如果需错误报告到服务器，则需要手动重写postReport()方法
 * @create 2014-7-2
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final boolean DEBUG = false; // 是否开启日志输出
    private static final String POSTFIX_NAME = ".log"; // 错误报告文件的扩展名
    private String toastMsg = "程序异常退出，请把日志发送给我们";

    /** 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;
    private Properties mDeviceCrashInfo; // 设备信息
    private Context mContext;
    private static CrashHandler instance = null;

    private CrashHandler() {}

    private synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public static void create(Context ctx) {
        create(ctx, null);
    }

    public static void create(Context ctx, String toastMsg) {
        CrashHandler crashHandler = getInstance();
        if (null != toastMsg) {
            crashHandler.toastMsg = toastMsg;
        }
        crashHandler.mContext = ctx.getApplicationContext();
        crashHandler.mDefaultHandler = Thread
                .getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!DEBUG) {
            return;
        }
        saveCrashInfoToFile(ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                // 让线程停止一会是为了显示Toast信息给用户
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            KJActivityManager.create().AppExit(mContext);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     * 
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (!StringUtils.isEmpty(toastMsg)) {
                    // 使用Toast来显示异常信息
                    // Toast 显示需要出现在一个线程的消息队列中
                    ViewInject.longToast(mContext, toastMsg);
                }
                Looper.loop();
            }
        }.start();
        return true;
    }

    /**
     * 保存错误信息到文件中
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put("device", result);

        String fileName = null;
        FileOutputStream trace = null;
        try {
            // 保存文件
            fileName = "kjlibrary-" + System.currentTimeMillis() + POSTFIX_NAME;
            trace = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            mDeviceCrashInfo.store(trace, "");
            trace.flush();
        } catch (Exception e) {
        } finally {
            FileUtils.closeIO(trace);
        }
        return fileName;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    /**
     * 获取错误报告文件名
     */
    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        // 实现FilenameFilter接口的类实例可用于过滤器文件名
        FilenameFilter filter = new FilenameFilter() {
            // accept(File dir, String name)
            // 测试指定文件是否应该包含在某一文件列表中。
            public boolean accept(File dir, String name) {
                return name.endsWith(POSTFIX_NAME);
            }
        };
        // list(FilenameFilter filter)
        // 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中满足指定过滤器的文件和目录
        return filesDir.list(filter);
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     * 
     * @param ctx
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                File cr = new File(ctx.getFilesDir(), fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }

    /**
     * 使用HTTP Post 发送错误报告到服务器
     */
    protected void postReport(File file) {}
}