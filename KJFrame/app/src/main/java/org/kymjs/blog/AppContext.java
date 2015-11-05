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
package org.kymjs.blog;

import android.app.Application;

import org.kymjs.kjframe.http.HttpConfig;

/**
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class AppContext extends Application {
    public static int screenW;
    public static int screenH;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpConfig.CACHEPATH = AppConfig.httpCachePath;
        CrashHandler.create(this);
    }
}
