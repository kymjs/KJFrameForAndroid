package org.kymjs.aframe.database.utils;

import java.lang.reflect.Field;

/**
 * @title 属性
 * @description 【非主键】的【基本数据类型】 都是属性
 * @version 1.0
 * @created 2012-10-10
 */
public class Property {

    private Field field; // 字段
    private String fieldName; // 属性名
    private String column; // 数据库中字段名

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

}
