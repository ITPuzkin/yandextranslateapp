package com.eroshin.victor.myapplication.events.TranslateEvent;

/**
 * Created by eroshin on 28.03.2017.
 */

public class TranslateEvent {
    private String s;

    public TranslateEvent(String str) {
        s = str;
    }

    public TranslateEvent() {
    }

    public String getS() {
        return s;
    }
}
