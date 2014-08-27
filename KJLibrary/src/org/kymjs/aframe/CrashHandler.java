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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.kymjs.aframe.http.KJFileParams;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.ui.KJActivityManager;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
 * 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. <br>
 * 
 * <b>警告</b> : 如果需错误报告到服务器，需要设置 protected boolean openUpload = true;
 * 且需要手动重写uploadLog()方法<br>
 * 
 * <b>创建时间</b> 2014-7-2
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private Context mContext;
    public boolean openUpload = true;
    // log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".log";
    private static CrashHandler sInstance = null;
    // 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private UncaughtExceptionHandler mDefaultCrashHandler;

    private CrashHandler(Context cxt) {
        // 获取系统默认的异常处理器
        mDefaultCrashHandler = Thread
                .getDefaultUncaughtExceptionHandler();
        // 将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 获取Context，方便内部使用
        mContext = cxt.getApplicationContext();
    }

    public synchronized static CrashHandler create(Context cxt) {
        if (sInstance == null) {
            sInstance = new CrashHandler(cxt);
        }
        return sInstance;
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // 导出异常信息到SD卡中
            saveToSDCard(ex);
        } catch (Exception e) {
        } finally {
            // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
            ViewInject.longToast(mContext, "您的手机可能不兼容该功能");
            // if (mDefaultCrashHandler != null) {
            // mDefaultCrashHandler.uncaughtException(thread, ex);
            // } else {
            // ex.printStackTrace();
            // KJActivityManager.create().finishAllActivity();
            // }
            ex.printStackTrace();
            KJActivityManager.create().finishAllActivity();
        }
    }

    private void saveToSDCard(Throwable ex) throws Exception {
        File file = FileUtils.getSaveFile("KJLog",
                SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss")
                        + FILE_NAME_SUFFIX);
        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new FileWriter(file)));
        // 导出发生异常的时间
        pw.println(SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss"));
        // 导出手机信息
        dumpPhoneInfo(pw);

        pw.println();
        // 导出异常的调用栈信息
        ex.printStackTrace(pw);
        pw.close();
    }

    private void dumpPhoneInfo(PrintWriter pw)
            throws NameNotFoundException {
        // 应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        pw.println();

        // android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        pw.println();

        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        pw.println();

        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        pw.println();

        // cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
        pw.println();
    }

    /**
     * 上传至服务器
     */
    protected void uploadLog() {
        KJHttp kjh = new KJHttp();
        KJFileParams params = new KJFileParams();
        String[] list = FileUtils.getSaveFolder("KJLog").list();
        try {
            for (String filePath : list) {
                params.put(new File(filePath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        kjh.post("", params, null);
        for (String filePath : list) {
            new File(filePath).delete();
        }
    }
}