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

import android.database.Cursor;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.database.utils.ManyToOne;
import org.kymjs.kjframe.database.utils.OneToMany;
import org.kymjs.kjframe.database.utils.Property;
import org.kymjs.kjframe.database.utils.TableInfo;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 游标操作的帮助类<br>
 * 
 * <b>创建时间</b> 2014-8-15
 * 
 * @author 杨福海 (www.yangfuhai.com)
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public class CursorHelper {
    /**
     * 获取一个已保存的JavaBean对象
     * 
     * @param cursor
     *            游标
     * @param clazz
     *            JavaBean.class
     * @param db
     *            KJDB对象引用
     * @return
     */
    public static <T> T getEntity(Cursor cursor, Class<T> clazz, KJDB db) {
        try {
            if (cursor != null) {
                // 读取表信息
                TableInfo table = TableInfo.get(clazz);
                // 读取列数
                int columnCount = cursor.getColumnCount();
                if (columnCount > 0) {
                    // 创建JavaBean对象
                    T entity = clazz.newInstance();
                    // 设置JavaBean的每一个属性
                    for (int i = 0; i < columnCount; i++) {
                        String column = cursor.getColumnName(i);
                        Property property = table.propertyMap.get(column);
                        if (property != null) {
                            property.setValue(entity, cursor.getString(i));
                        } else {
                            if (table.getId().getColumn().equals(column)) {
                                table.getId().setValue(entity,
                                        cursor.getString(i));
                            }
                        }

                    }
                    /**
                     * 处理OneToMany的lazyLoad形式
                     */
                    for (OneToMany oneToManyProp : table.oneToManyMap.values()) {
                        if (oneToManyProp.getDataType() == OneToManyLazyLoader.class) {
                            OneToManyLazyLoader oneToManyLazyLoader = new OneToManyLazyLoader(
                                    entity, clazz, oneToManyProp.getOneClass(),
                                    db);
                            oneToManyProp.setValue(entity, oneToManyLazyLoader);
                        }
                    }

                    /**
                     * 处理ManyToOne的lazyLoad形式
                     */
                    for (ManyToOne manyToOneProp : table.manyToOneMap.values()) {
                        if (manyToOneProp.getDataType() == ManyToOneLazyLoader.class) {
                            ManyToOneLazyLoader manyToOneLazyLoader = new ManyToOneLazyLoader(
                                    entity, clazz,
                                    manyToOneProp.getManyClass(), db);
                            manyToOneProp.setValue(entity, manyToOneLazyLoader);
                        }
                    }
                    return entity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据库的模型（将数据库转换为map集合）
     * 
     * @param cursor
     * @return
     */
    public static DbModel getDbModel(Cursor cursor) {
        if (cursor != null && cursor.getColumnCount() > 0) {
            DbModel model = new DbModel();
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                model.set(cursor.getColumnName(i), cursor.getString(i));
            }
            return model;
        }
        return null;
    }

    /**
     * 将数据库模型转换为 JavaBean对象
     * 
     * @param dbModel
     * @param clazz
     *            待生成的JavaBean对象
     */
    public static <T> T dbModel2Entity(DbModel dbModel, Class<?> clazz) {
        if (dbModel != null) {
            HashMap<String, Object> dataMap = dbModel.getDataMap();
            try {
                @SuppressWarnings("unchecked")
                T entity = (T) clazz.newInstance();
                for (Entry<String, Object> entry : dataMap.entrySet()) {
                    String column = entry.getKey();
                    TableInfo table = TableInfo.get(clazz);
                    Property property = table.propertyMap.get(column);
                    if (property != null) {
                        property.setValue(entity,
                                entry.getValue() == null ? null : entry
                                        .getValue().toString());
                    } else {
                        if (table.getId().getColumn().equals(column)) {
                            table.getId().setValue(
                                    entity,
                                    entry.getValue() == null ? null : entry
                                            .getValue().toString());
                        }
                    }

                }
                return entity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
