package com.eroshin.victor.myapplication.bd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.eroshin.victor.myapplication.R;

import java.util.HashSet;

/**
 * Created by eroshin on 30.03.2017.
 */

public class AutoCompleteAdapter {

    public HashSet<String> strings;
    DBHelper dbHelper;
    Context ctx;


    public AutoCompleteAdapter(Context c, int i, DBHelper d) {
        strings = new HashSet<>();
        dbHelper = d;
        ctx = c;
    }

    public String[] getList() {
        String[] answ = new String[strings.size()];
        return strings.toArray(answ);
    }

    public void init(String str) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Toast.makeText(ctx, ctx.getString(R.string.dbError), Toast.LENGTH_SHORT).show();
            return;
        }

        strings.clear();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, str, null, null, null, "datecreate desc");
        if (c.moveToFirst()) {
            int text = c.getColumnIndex("fromtext");
            int totext = c.getColumnIndex("totext");
            do {
                strings.add(c.getString(text));
                strings.add(c.getString(totext));
            } while (c.moveToNext());
        }
        c.close();
    }


}
