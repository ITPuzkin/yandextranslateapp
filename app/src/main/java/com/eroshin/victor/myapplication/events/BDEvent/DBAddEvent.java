package com.eroshin.victor.myapplication.events.BDEvent;

/**
 * Created by eroshin on 29.03.2017.
 */

public class DBAddEvent {
    public String from;
    public String to;
    public long date;

    public String fromindex;
    public String toindex;

    public String isFav;
    public String isDel;

    public int type;// 0 -add     1-del

    public DBAddEvent(String s1, String s2, long d, int t, String fri, String toi, String fav, String del) {
        from = s1;
        to = s2;
        date = d;
        type = t;
        fromindex = fri;
        toindex = toi;
        isFav = fav;
        isDel = del;
    }

}
