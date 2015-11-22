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

import android.content.Context;

import org.kymjs.kjframe.KJDB.DbUpdateListener;
import org.kymjs.kjframe.utils.KJLoger;

/**
 * 数据库配置器<br>
 * 
 * <b>创建时间</b> 2014-8-15
 * 
 * @author kymjs (https://github.com/kymjs)
 * @author 杨福海 (www.yangfuhai.com)
 * @version 1.0
 */
final public class DaoConfig {
    private Context mContext = null; // android上下文
    private String mDbName = "KJLibrary.db"; // 数据库名字
    private int dbVersion = 1; // 数据库版本
    private boolean debug = KJLoger.DEBUG_LOG; // 是否是调试模式（调试模式 增删改查的时候显示SQL语句）
    private DbUpdateListener dbUpdateListener;
    // private boolean saveOnSDCard = false;//是否保存到SD卡
    private String targetDirectory;// 数据库文件在sd卡中的目录

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    /**
     * 数据库名
     */
    public String getDbName() {
        return mDbName;
    }

    /**
     * 数据库名
     */
    public void setDbName(String dbName) {
        this.mDbName = dbName;
    }

    /**
     * 数据库版本
     */
    public int getDbVersion() {
        return dbVersion;
    }

    /**
     * 数据库版本
     */
    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     * 是否调试模式
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 是否调试模式
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 数据库升级时监听器
     * 
     * @return
     */
    public DbUpdateListener getDbUpdateListener() {
        return dbUpdateListener;
    }

    /**
     * 数据库升级时监听器
     */
    public void setDbUpdateListener(DbUpdateListener dbUpdateListener) {
        this.dbUpdateListener = dbUpdateListener;
    }

    // public boolean isSaveOnSDCard() {
    // return saveOnSDCard;
    // }
    //
    // public void setSaveOnSDCard(boolean saveOnSDCard) {
    // this.saveOnSDCard = saveOnSDCard;
    // }

    /**
     * 数据库文件在sd卡中的目录
     */
    public String getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * 数据库文件在sd卡中的目录
     */
    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }
}