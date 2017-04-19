package com.eroshin.victor.myapplication.events.BDEvent;

/**
 * Created by eroshin on 30.03.2017.
 */

public class GetPosEvent {
    public String str;
    public boolean fav = false;

    public GetPosEvent(String s) {
        str = s;
    }

    public GetPosEvent(String s, boolean b) {
        str = s;
        fav = b;
    }
}
