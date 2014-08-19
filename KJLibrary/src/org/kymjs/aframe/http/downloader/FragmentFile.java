/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.aframe.http.downloader;

import org.kymjs.aframe.core.SparseIntArray;
import org.kymjs.aframe.ui.KJActivityManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 碎片文件保存到数据库的工具类<br>
 * 
 * <b>创建时间</b> 2014-8-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class FragmentFile {
    private DBOpenHelper openHelper;

    public FragmentFile() {
        Context cxt = null;
        try {
            cxt = KJActivityManager.create().topActivity();
        } catch (Exception e) {
            cxt = KJActivityManager.create().topActivity()
                    .getApplicationContext();
        }
        openHelper = new DBOpenHelper(cxt);
    }

    /**
     * 获取每条线程已经下载的文件长度
     */
    public SparseIntArray getData(String path) {
        SparseIntArray data = new SparseIntArray();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select threadid, len from log where path=?",
                new String[] { path });
        for (cursor.moveToFirst(); cursor.moveToNext();) {
            data.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     * 
     * @param path
     * @param map
     */
    public void save(String path, SparseIntArray map) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < map.size(); i++) {
                db.execSQL(
                        "insert into log(path, threadid, len) values(?,?,?)",
                        new Object[] { path, map.keyAt(i), map.valueAt(i) });
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     * 
     * @param path
     * @param map
     */
    public void update(String path, SparseIntArray map) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < map.size(); i++) {
                db.execSQL("update log set len=? where path=? and threadid=?",
                        new Object[] { map.valueAt(i), path, map.keyAt(i) });
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     */
    public void delete(String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from log where path=?", new Object[] { path });
        db.close();
    }

    private class DBOpenHelper extends SQLiteOpenHelper {
        private static final String DBNAME = "kjLibraryDownload.db";

        public DBOpenHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS log (id integer primary key autoincrement, path varchar(150), threadid INTEGER, len INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS log");
            onCreate(db);
        }
    }
}
