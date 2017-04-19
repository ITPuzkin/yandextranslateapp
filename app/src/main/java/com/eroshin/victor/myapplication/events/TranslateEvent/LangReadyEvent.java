package com.eroshin.victor.myapplication.events.TranslateEvent;

/**
 * Created by eroshin on 28.03.2017.
 */

public class LangReadyEvent {
    public boolean isFrom;

    public LangReadyEvent(boolean isfrom) {
        isFrom = isfrom;
    }
}
