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

/**
 * KJLibrary's base exception class
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-2-28
 */
public class KJException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public KJException() {
        super();
    }

    public KJException(String msg) {
        super(msg);
    }

    public KJException(Throwable ex) {
        super(ex);
    }

    public KJException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
