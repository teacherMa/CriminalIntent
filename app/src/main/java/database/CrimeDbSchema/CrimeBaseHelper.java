package database.CrimeDbSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.CrimeDbSchema.CrimeDbSchema.CrimeTable;

/**
 * Created by xiaomage on 2016/12/28.
 */

/*这个类要做的事情很简单
* 如果数据库已经存在了就直接打开，
* 如果数据库不存在就调用onCreate方法创建一个
* 某些情况下还能调用onUpgrade方法升级数据库*/

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";


    public CrimeBaseHelper(Context context) {
        super(context , DATABASE_NAME , null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*这里发生过一起一个空格引发的血案  create table 后面曾经没有加空格然后导致RuntimeException*/
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED +", " +
                CrimeTable.Cols.SUSPECT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
