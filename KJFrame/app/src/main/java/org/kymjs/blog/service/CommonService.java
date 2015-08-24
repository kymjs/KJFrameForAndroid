/*
 * Copyright (c) 2015, 张涛.
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
package org.kymjs.blog.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.kymjs.blog.AppConfig;
import org.kymjs.blog.CrashHandler;
import org.kymjs.blog.utils.MD5;
import org.kymjs.blog.utils.MailSenderInfo;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.SimpleMailSender;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.utils.SystemTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 做一些全局的信息处理，检查更新，日志上传
 *
 * @author kymjs (http://www.kymjs.com/)
 */
public class CommonService extends IntentService {

    private final KJHttp kjh;

    public CommonService() {
        super("CommonService");
        HttpConfig config = new HttpConfig();
        config.cacheTime = 0;
        kjh = new KJHttp(config);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        File file = new File(FileUtils.getSaveFolder(AppConfig.saveFolder)
                + "/kjblog.apk");
        if (file.exists()) {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(),
                    PackageManager.GET_ACTIVITIES);
            if (info.versionCode <= SystemTool.getAppVersionCode(this)) {
                file.delete();
            }
        }

        kjh.get("http://www.kymjs.com/api/version", new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug("检测更新===" + t);
                checkVersion(t);
            }
        });
        File crashLogFile = FileUtils.getSaveFile(AppConfig.saveFolder,
                CrashHandler.FILE_NAME_SUFFIX);
        if (crashLogFile != null && crashLogFile.exists()) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bfr = null;
            try {
                bfr = new BufferedReader(new FileReader(crashLogFile));
                String line = null;
                do {
                    line = bfr.readLine();
                    sb.append(line).append("\n");
                } while (line != null);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                FileUtils.closeIO(bfr);
            }
            crashLogFile.delete();
            if (sb.length() > 30) {
                uploadCrashLog(sb.toString());
            }
        }
    }

    private void checkVersion(String json) {
        final String url = Parser.checkVersion(CommonService.this, json);
        if (!StringUtils.isEmpty(url) && SystemTool.isWiFi(this)) {
            download(url);
        }
    }

    private void download(String url) {
        final File folder = FileUtils.getSaveFolder(AppConfig.saveFolder);
        File tempFile = new File(folder + "/kjblog.apk.tmp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        ViewInject.toast("正在为你下载新版本");
        kjh.download(folder + "/kjblog.apk", url, new HttpCallBack() {
            /**
             * 下载过程
             */
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            /**
             * 下载完成，开始安装
             */
            @Override
            public void onSuccess(byte[] t) {
                super.onSuccess(t);
                SystemTool.installApk(CommonService.this, new File(folder
                        + "/kjblog.apk"));
            }
        });
    }

    private void uploadCrashLog(String info) {
        if ("96ee32139bbefde1033340fdf346f81f".equals(getSign(this,
                "org.kymjs.blog"))) {
            try {
                MailSenderInfo mailInfo = new MailSenderInfo();
                mailInfo.setMailServerHost("smtp.qq.com");
                mailInfo.setMailServerPort("25");
                mailInfo.setValidate(true);
                mailInfo.setUserName("1182954373@qq.com");
                mailInfo.setPassword("kymjs123");
                mailInfo.setFromAddress("1182954373@qq.com");
                mailInfo.setToAddress("766136833@qq.com");
                mailInfo.setSubject("错误日志");
                mailInfo.setContent(info);

                // 这个类主要来发送邮件
                SimpleMailSender sms = new SimpleMailSender();
                sms.sendTextMail(mailInfo);// 发送文体格式
                // sms.sendHtmlMail(mailInfo);//发送html格式
            } catch (Exception e) {
            }
        }
    }

    public static String getSign(Context context, String pkgName) {
        try {
            PackageInfo pis = context.getPackageManager().getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
            return MD5.hexdigest(pis.signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
