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
package org.kymjs.aframe.ui;

/**
 * 规范Activity中广播接受者注册的接口协议
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public interface I_BroadcastReg {
    /**
     * 注册广播
     */
    void registerBroadcast();

    /**
     * 解除注册广播
     */
    void unRegisterBroadcast();
}
