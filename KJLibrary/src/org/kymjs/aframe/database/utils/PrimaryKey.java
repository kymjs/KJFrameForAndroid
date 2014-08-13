package org.kymjs.aframe.database.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键配置
 * 
 * @explain 不配置的时候默认找类的id字段作为主键，column不配置的是默认为字段名
 * @version 1.0
 * @created 2014-8-13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    public String column() default "";
}
