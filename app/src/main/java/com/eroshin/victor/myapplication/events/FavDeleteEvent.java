package com.eroshin.victor.myapplication.events;

/**
 * Created by eroshin on 30.03.2017.
 */

public class FavDeleteEvent {
    public int id;
    public String isDel;
    public FavDeleteEvent(int i,String del){id=i;isDel = del;}
}
