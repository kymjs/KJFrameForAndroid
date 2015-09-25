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
package org.kymjs.blog.receiver;

/**
 * 使用KJPush(http://git.oschina.net/kymjs/KJPush)项目，获取推送信息的广播，
 * 其中屏幕开关与网络改变的广播仅在应用未退出时有效
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class KJPushReceiver {
    // extends PushReceiver {
    //
    // private final KJHttp kjh = new KJHttp();
    //
    // static Handler handle = new Handler() {};
    // Runnable stopPush = new Runnable() {
    // @Override
    // public void run() {
    // KJPushManager.create().stopWork();
    // }
    // };
    //
    // @Override
    // public void onNetworkStateChange(Context context, Intent intent) {
    // boolean isWifi = SystemTool.isWiFi(context);
    // if (isWifi) {
    // KJPushConfig.PALPITATE_TIME = 120000;
    // } else {
    // KJPushConfig.PALPITATE_TIME = 300000;
    // }
    // }
    //
    // @Override
    // public void onScreenOff(Context context, Intent intent) {
    // handle.postDelayed(stopPush, 300000);
    // }
    //
    // @Override
    // public void onScreenOn(Context context, Intent intent) {
    // handle.removeCallbacks(stopPush);
    // KJPushManager.create().startWork(context, KJPushReceiver.class);
    // }
    //
    // @Override
    // public void onTryPullData(final Context context, Intent intent) {
    // super.onTryPullData(context, intent);
    // kjh.get("http://www.kymjs.com/new_message", new HttpCallBack() {
    // @Override
    // public void onSuccess(String t) {
    // super.onSuccess(t);
    // if ("true".equalsIgnoreCase(t)) {
    // context.sendBroadcast(new Intent(
    // AppConfig.PUSH_BROADCAST_ACTION));
    // }
    // }
    // });
    // }
    //
    // @Override
    // public void onPullData(Context context, Intent intent) {}
}
