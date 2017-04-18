package com.eroshin.victor.myapplication.events.BDEvent;

/**
 * Created by eroshin on 30.03.2017.
 */

public class FavAddFromHistory {
    public int id;
    public String isFav;
    public FavAddFromHistory(int i,String del){id=i;
        isFav = del;}
}
