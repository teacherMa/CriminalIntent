package com.example.xiaomage.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;
import database.CrimeDbSchema.CrimeCursorWrapper;
import database.CrimeDbSchema.CrimeDbSchema.CrimeTable;

/**
 * Created by xiaomage on 2016/12/9.
 */

/* 从手机内存中读取数据，而不是从运行内存中读取数据，
* */



public class CrimeLab {
    public static final int MAX_CRIMEA_MOUNT = 1000;
    private static CrimeLab sCrimeLab;
    private static boolean[] sModifyArray = new boolean[MAX_CRIMEA_MOUNT];
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            return new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public static boolean[] getModifyArray(){
        return sModifyArray;
    }

    public static void setModifyArray(int position){
        sModifyArray[position] = true;
    }

    public static void Initialize(){
        for(int i =0;i<MAX_CRIMEA_MOUNT;i++){
            sModifyArray[i] = false;
        }
    }

    public void addCrime(Crime crime){
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME,null,contentValues);
    }

    public void removeCrime(int position){
        List<Crime> crimes = getCrimes();
        String uuidString = crimes.get(position).getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?",new String[]{ uuidString });
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        if(sCrimeLab == null){
            sCrimeLab = CrimeLab.this;
        }
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null,null);//null表明读取CrimeTable中的所有数据
        try{
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()){
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id){

        CrimeCursorWrapper cursorWrapper = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String []{id.toString()}
        );

        try{
            if(cursorWrapper.getCount() == 0){
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }finally {
            cursorWrapper.close();
        }

    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues contentValues  = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME,contentValues,CrimeTable.Cols.UUID + " = ?",new String[]{ uuidString });
    }


    /*封装crime的信息方便database.update()和database.insert()*/
    private static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID,crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE,crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED,crime.isSolved()?1:0);
        contentValues.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());

        return contentValues;
    }


    /*根据参数读取CrimeTable数据表中的内容*/
    private CrimeCursorWrapper queryCrimes(String whereClause, String []whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
}
