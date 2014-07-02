/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
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
package net.tsz.afinal.utils;

public class DBException extends AfinalException {
    private static final long serialVersionUID = 1L;

    public DBException() {
    }

    public DBException(String msg) {
        super(msg);
    }

    public DBException(Throwable ex) {
        super(ex);
    }

    public DBException(String msg, Throwable ex) {
        super(msg, ex);
    }

}
