/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
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
package org.kymjs.kjframe.database;

import java.util.LinkedList;

/**
 * sql语句的全部信息<br>
 * 
 * <b>创建时间</b> 2014-8-15
 *
 * @author kymjs (http://www.kymjs.com)
 * @author 杨福海 (http://www.yangfuhai.com)
 * @version 1.0
 */
public class SqlInfo {

    private String sql;
    private LinkedList<Object> bindArgs;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedList<Object> getBindArgs() {
        return bindArgs;
    }

    public void setBindArgs(LinkedList<Object> bindArgs) {
        this.bindArgs = bindArgs;
    }

    public Object[] getBindArgsAsArray() {
        if (bindArgs != null)
            return bindArgs.toArray();
        return null;
    }

    public String[] getBindArgsAsStringArray() {
        if (bindArgs != null) {
            String[] strings = new String[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++) {
                strings[i] = bindArgs.get(i).toString();
            }
            return strings;
        }
        return null;
    }

    public void addValue(Object obj) {
        if (bindArgs == null)
            bindArgs = new LinkedList<Object>();

        bindArgs.add(obj);
    }

}
