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
package org.kymjs.blog.ui;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.kymjs.blog.AppContext;
import org.kymjs.blog.R;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

/**
 * 开机界面
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class AppStart extends KJActivity {
    public static String TAG = "appstart";

    @Override
    public void setRootView() {
        ImageView image = new ImageView(aty);
        image.setImageResource(R.drawable.splash_bg);
        Animation anim = AnimationUtils.loadAnimation(aty, R.anim.splash_start);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                jumpTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        image.setAnimation(anim);
        setContentView(image);
        AppContext.screenH = DensityUtils.getScreenH(aty);
        AppContext.screenW = DensityUtils.getScreenW(aty);
    }

    /**
     * 根据设置启动推送进程
     */
    private void configPush() {}

    private void jumpTo() {
//        startService(new Intent(aty, CommonService.class));
        configPush();
        boolean isFirst = PreferenceHelper.readBoolean(aty, TAG, "first_open",
                true);
        Intent jumpIntent = new Intent();
        if (!isFirst) {
            jumpIntent.setClass(aty, Main.class);
        } else {
            PreferenceHelper.write(aty, TAG, "first_open", false);
            jumpIntent.setClass(aty, Splash.class);
        }
        startActivity(jumpIntent);
        finish();
    }
}
