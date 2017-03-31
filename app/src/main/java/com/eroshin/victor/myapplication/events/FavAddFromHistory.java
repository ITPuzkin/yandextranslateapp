package com.eroshin.victor.myapplication.events;

import android.database.Cursor;

import com.eroshin.victor.myapplication.bd.FavAdapter;

/**
 * Created by eroshin on 30.03.2017.
 */

public class FavAddFromHistory {
    public int id;
    public String isDel;
    public FavAddFromHistory(int i,String del){id=i;isDel = del;}
}
