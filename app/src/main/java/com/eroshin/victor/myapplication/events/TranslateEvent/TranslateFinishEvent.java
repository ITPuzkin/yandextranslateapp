package com.eroshin.victor.myapplication.events.TranslateEvent;

/**
 * Created by eroshin on 28.03.2017.
 */

public class TranslateFinishEvent {
    private String s;
    public TranslateFinishEvent(String str){s = str;}
    public String getS(){return s;}
}
