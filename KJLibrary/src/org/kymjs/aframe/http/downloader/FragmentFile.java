package org.kymjs.aframe.http.downloader;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FragmentFile {
    private DBOpenHelper openHelper;

    public FragmentFile(Context context) {
        openHelper = new DBOpenHelper(context);
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
    public void save(String path, Map<Integer, Integer> map) {// int threadid,
                                                              // int position
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL(
                        "insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
                        new Object[] { path, entry.getKey(), entry.getValue() });
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     * 
     * @param path
     * @param map
     */
    public void update(String path, Map<Integer, Integer> map) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL(
                        "update filedownlog set downlength=? where downpath=? and threadid=?",
                        new Object[] { entry.getValue(), path, entry.getKey() });
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     */
    public void delete(String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from filedownlog where downpath=?",
                new Object[] { path });
        db.close();
    }

    private class DBOpenHelper extends SQLiteOpenHelper {
        private static final String DBNAME = "kjLibraryDownload.db";

        public DBOpenHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, url varchar(150), threadid INTEGER, len INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS filedownlog");
            onCreate(db);
        }
    }
}
