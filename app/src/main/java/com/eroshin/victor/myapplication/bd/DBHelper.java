package com.eroshin.victor.myapplication.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by eroshin on 29.03.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context c){
        super(c,"myDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper","---on create");

        db.execSQL("create table mytable (" +
                "_id integer primary key autoincrement," +
                "fromtext text not null," +
                "totext text not null," +
                "datecreate text not null," +
                "langfrom text not null," +
                "langto text not null," +
                "fav text not null," +
                "del text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
