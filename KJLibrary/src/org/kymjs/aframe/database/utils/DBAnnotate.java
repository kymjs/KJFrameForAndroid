package org.kymjs.aframe.database.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.kymjs.aframe.KJException;

public class DBAnnotate {
    /**
     * 返回表名
     * 
     * @param clazz
     * @return 如果有Table注解，则返回Table名字，否则返回由"_"替换"."的完整类名
     */
    public static String getTableName(Class<?> clazz) {
        // 根据实体类 获得 实体类对应的表名
        Table table = clazz.getAnnotation(Table.class);
        if (table == null || table.name().trim().length() == 0) {
            // 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
            return clazz.getName().replace('.', '_');
        } else {
            return table.name();
        }
    }

    /**
     * 返回主键名
     * 
     * @param clazz
     * @return 如果有PrimaryKey注解，返回注解名，否则返回id字段
     * @throws KJException
     *             如果没有PrimaryKey/id
     */
    public static String getPrimaryKey(Class<?> clazz) {
        String primaryKey = null; // 主键名
        // 获取全部属性
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {
            PrimaryKey id = null;
            Field idField = null;
            // 遍历，查找有ID注解的属性
            for (Field field : fields) {
                id = field.getAnnotation(PrimaryKey.class);
                if (id != null) {
                    idField = field;
                    break;
                }
            }

            // 有ID注解
            if (id != null) {
                primaryKey = id.column();
                // id注解默认值为属性名
                if (primaryKey == null || primaryKey.trim().length() == 0) {
                    primaryKey = idField.getName();
                }
            } else { // 没有ID注解,默认去找 id 为主键
                for (Field field : fields) {
                    if ("id".equals(field.getName())) {
                        return "id";
                    }
                }
            }
        } else {
            throw new KJException("this model[" + clazz + "] has no field");
        }
        return primaryKey;
    }

    /**
     * 返回主键的字段
     * 
     * @param clazz
     * @return 如果有PrimaryKey注解，返回被注解的字段，否则返回id字段
     * @throws KJException
     *             如果没有PrimaryKey/id
     */
    public static Field getPrimaryField(Class<?> clazz) {
        // 获取全部属性
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {
            PrimaryKey id = null;
            // 遍历，查找有ID注解的属性
            for (Field field : fields) {
                id = field.getAnnotation(PrimaryKey.class);
                if (id != null) {
                    return field;
                }
            }
            for (Field field : fields) {
                if ("id".equals(field.getName())) {
                    return field;
                }
            }
        } else {
            throw new KJException("this model[" + clazz
                    + "] has no Primary field");
        }
        return null;
    }

    /**
     * 返回类除主键外的全部属性
     * 
     * @param clazz
     * @return
     */
    public static ArrayList<Property> getFieldList(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        ArrayList<Property> plist = new ArrayList<Property>();
        Field primaryField = getPrimaryField(clazz);
        for (Field field : fields) {
            // 过滤主键
            if (field.getName().equals(primaryField)) {
                continue;
            } else {
                Property property = new Property();
                property.setFieldName(field.getName());
                property.setColumn(field.getName() + "_");
                property.setField(field);
                plist.add(property);
            }
        }
        return plist;
    }
}
