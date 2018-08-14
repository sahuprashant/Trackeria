package com.example.track.trackeria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackDatabase extends SQLiteOpenHelper {
    public static final String DATABASE = "trackdata.db";
    public static final String TABLE = "current";
    public static final String col_1 = "Id";
    public static final String col_2 = "lat";
    public static final String col_3 = "lon";

    public TrackDatabase(Context context) {
        super(context, DATABASE, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String stat = "create table "+TABLE+" (Id INTEGER PRIMARY KEY AUTOINCREMENT,lat DOUBLE,lon DOUBLE)";
        sqLiteDatabase.execSQL(stat);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertdata(double lat,double lon){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(col_2,lat);
        val.put(col_3,lon);
        long result = sqLiteDatabase.insert(TABLE,null,val);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select lat,lon from "+TABLE,null);
        return res;
    }

    public void resetdata(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE);
    }
}
