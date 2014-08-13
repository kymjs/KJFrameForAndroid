package org.kymjs.aframe.database;

import org.kymjs.aframe.utils.FileUtils;

import android.content.Context;

public class DaoConfig {
    boolean debug; // 是否是调试模式（调试模式 增删改查的时候显示SQL语句）
    Context cxt;
    String dbName; // 数据库名字
    int dbVersion; // 数据库版本
    String dbPath; // 数据库路径

    public DaoConfig() {
        debug = false;
        dbName = "kjLibrary.db";
        dbVersion = 1;
        dbPath = FileUtils.getSaveFile("KJLibrary", dbName).getAbsolutePath();
    }
}
