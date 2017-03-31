package com.eroshin.victor.myapplication.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.eroshin.victor.myapplication.bd.DBHelper;

import java.util.ArrayList;

/**
 * Created by eroshin on 30.03.2017.
 */

public class AutoCompleteAdapter {

    public ArrayList<String> strings;
    DBHelper dbHelper;
    Context ctx;

    public AutoCompleteAdapter(Context c,int i,DBHelper d){
        strings = new ArrayList<>();
        dbHelper = d;
        ctx = c;
    }

    public void init(){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(ctx,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor c = db.query("myTable",null,"del=0",null,null,null,null);
        if(c.moveToFirst()){
            int text = c.getColumnIndex("fromtext");
            do{
                strings.add(c.getString(text));
            }while (c.moveToNext());
        }
    }



}
