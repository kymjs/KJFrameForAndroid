package org.kymjs.aframe.database;

import java.util.ArrayList;

import org.kymjs.aframe.KJException;
import org.kymjs.aframe.database.utils.DBAnnotate;
import org.kymjs.aframe.database.utils.Property;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * database class,The DBLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-14
 */
public class KJDB {
    private static KJDB instance;
    public static DaoConfig config;
    private DBOpenHelper openHelper;

    private KJDB(DaoConfig config) {
        KJDB.config = config;
        if (config.cxt != null) {
            openHelper = new DBOpenHelper(config.cxt);
        } else {
            throw new KJException("context is empty");
        }
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @return
     */
    public static KJDB create(Context cxt) {
        DaoConfig config = new DaoConfig();
        config.cxt = cxt;
        return create(config);
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @param isDebug
     *            是否打印sql语句
     * @return
     */
    public static KJDB create(Context cxt, boolean isDebug) {
        DaoConfig config = new DaoConfig();
        config.cxt = cxt;
        config.debug = isDebug;
        return create(config);
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @param dbName
     *            数据库名
     * @return
     */
    public static KJDB create(Context cxt, String dbName) {
        DaoConfig config = new DaoConfig();
        config.dbName = dbName;
        config.cxt = cxt;
        return create(config);
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @param dbName
     *            数据库名
     * @param isDebug
     *            是否打印sql语句
     * @return
     */
    public static KJDB create(Context cxt, String dbName, boolean isDebug) {
        DaoConfig config = new DaoConfig();
        config.dbName = dbName;
        config.cxt = cxt;
        config.debug = isDebug;
        return create(config);
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @param dbName
     *            数据库名
     * @param path
     *            数据库路径
     * @return
     */
    public static KJDB create(Context cxt, String dbName, String path) {
        DaoConfig config = new DaoConfig();
        config.cxt = cxt;
        config.dbName = dbName;
        config.dbPath = path;
        return create(config);
    }

    /**
     * 创建一个KJDB
     * 
     * @param cxt
     *            上下文对象
     * @param dbName
     *            数据库名
     * @param path
     *            数据库路径
     * @param isDebug
     *            是否打印sql语句
     * @return
     */
    public static KJDB create(Context cxt, String dbName, String path,
            boolean isDebug) {
        DaoConfig config = new DaoConfig();
        config.cxt = cxt;
        config.dbName = dbName;
        config.debug = isDebug;
        config.dbPath = path;
        return create(config);
    }

    /**
     * 创建一个DBLibrary对象(标准构造器)
     * 
     * @param cxt
     */
    public synchronized static KJDB create(DaoConfig config) {
        if (instance == null) {
            instance = new KJDB(config);
        }
        return instance;
    }

    /******************************* DBlibrary method *******************************/
    public void save(Object javaBean) {
        save(javaBean, null);
    }

    public void save(Object javaBean, String path) {
        SQLiteDatabase db;
        if (path == null) {
            db = getDataBase();
        } else {
            db = getDataBase(path);
        }
        checkedTable(db, javaBean.getClass());
        StringBuilder str = new StringBuilder();
        str.append("insert into ? (");
        db.execSQL("insert into ? (?,?) values (?,?);", new String[] {});
    }

    /******************************* private utils method *******************************/
    /**
     * 获取一个数据库
     * 
     * @param path
     *            数据库所在绝对路径
     */
    private SQLiteDatabase getDataBase(String path) {
        config.dbPath = path;
        return getDataBase();
    }

    /**
     * 获取一个数据库
     */
    private SQLiteDatabase getDataBase() {
        return SQLiteDatabase.openOrCreateDatabase(config.dbPath, null);
    }

    /**
     * 检测指定表是否存在，若不存在则创建
     * 
     * @param db
     *            要检测的数据库
     * @param clazz
     *            要检测的JavaBean所对应的表
     */
    private void checkedTable(SQLiteDatabase db, Class<?> clazz) {
        StringBuilder str = new StringBuilder();
        ArrayList<Property> plist = DBAnnotate.getFieldList(clazz);
        String[] datas = new String[plist.size() + 1];
        datas[0] = DBAnnotate.getTableName(clazz);
        str.append("create table if not exists ? (id integer primary key autoincrement");
        for (int i = 0; i < plist.size(); i++) {
            str.append(",?");
            datas[i + 1] = plist.get(i).getColumn();
        }
        str.append(")");
        db.execSQL(str.toString(), datas);
        str = null;
    }

    /**
     * 数据库帮助类
     */
    private class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, config.dbName, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}
