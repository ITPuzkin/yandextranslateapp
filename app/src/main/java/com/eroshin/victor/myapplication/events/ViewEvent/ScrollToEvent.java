package com.eroshin.victor.myapplication.events.ViewEvent;

/**
 * Created by eroshin on 30.03.2017.
 */

public class ScrollToEvent {
    public int position;
    public boolean fav = false;

    public ScrollToEvent(int p) {
        position = p;
    }

    public ScrollToEvent(int p, boolean b) {
        position = p;
        fav = b;
    }
}
