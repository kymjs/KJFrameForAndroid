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
package org.kymjs.kjframe.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

/**
 * CJFrameActivity接口协议，插件Activity必须实现此接口<br>
 * Activity实现此接口意味着将插件的Activity生命周期交給CJFrame托管， 而不再是ActivityManager托管<br>
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public interface I_CJActivity {
    public void onCreate(Bundle savedInstanceState);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroy();

    public void onRestart();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onSaveInstanceState(Bundle outState);

    public void onNewIntent(Intent intent);

    public void onRestoreInstanceState(Bundle savedInstanceState);

    public boolean onTouchEvent(MotionEvent event);

    public boolean onKeyUp(int keyCode, KeyEvent event);

    public void onWindowAttributesChanged(LayoutParams params);

    public void onWindowFocusChanged(boolean hasFocus);

    public void onBackPressed();

    /**
     * 设置托管Activity，并将that指针指向那个托管的Activity
     */
    public void setProxy(Activity proxyActivity, String dexPath);
}
