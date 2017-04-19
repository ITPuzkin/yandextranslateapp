package com.eroshin.victor.myapplication.events.BDEvent;

/**
 * Created by eroshin on 29.03.2017.
 */

// при добавлении/удалении из списка избранных
public class DBUpdateEvent {
    public String isfav;

    public DBUpdateEvent(String fav) {
        isfav = fav;
    }
}
