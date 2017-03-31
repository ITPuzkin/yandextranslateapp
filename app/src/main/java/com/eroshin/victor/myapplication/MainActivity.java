package com.eroshin.victor.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eroshin.victor.myapplication.bd.DBHelper;
import com.eroshin.victor.myapplication.events.ClearDbEvent;
import com.eroshin.victor.myapplication.events.ClearEditTextEvent;
import com.eroshin.victor.myapplication.events.DBAddEvent;
import com.eroshin.victor.myapplication.events.DBUpdateEvent;
import com.eroshin.victor.myapplication.events.DellHistEvent;
import com.eroshin.victor.myapplication.events.FavAddFromHistory;
import com.eroshin.victor.myapplication.events.FavButtonCheck;
import com.eroshin.victor.myapplication.events.FavClearEvent;
import com.eroshin.victor.myapplication.events.FavDeleteEvent;
import com.eroshin.victor.myapplication.events.GetPosEvent;
import com.eroshin.victor.myapplication.events.ScrollTo;
import com.eroshin.victor.myapplication.events.TranslateStarEvent;
import com.eroshin.victor.myapplication.events.UPdateFavListEvent;
import com.eroshin.victor.myapplication.events.UpdateHistListEvent;
import com.eroshin.victor.myapplication.fragments.FavoriteFragment;
import com.eroshin.victor.myapplication.fragments.HistoryFragment;
import com.eroshin.victor.myapplication.fragments.TranslateFragment;
import com.eroshin.victor.myapplication.fragments.testFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 28.03.2017.
 */

public class MainActivity extends AppCompatActivity{

    TabLayout tabs;
    ViewPager pager;
    Toolbar toolbar;

    public static DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainactivity);
        tabs = (TabLayout) findViewById(R.id.tabLayout);
        pager = (ViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setupViewPager(pager);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        tabs.setupWithViewPager(pager);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TranslateFragment(), getString(R.string.tab1_translate));
        adapter.addFragment(new FavoriteFragment(),getString(R.string.tab2_favorites));
        adapter.addFragment(new HistoryFragment(),getString(R.string.tab3_history));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(DellHistEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(event..equals("1")){
            ContentValues cv = new ContentValues();
            cv.put("del","1");
            db.update("mytable",cv,"_id="+event.cursor.getInt(id),null);
        }
        else{
            db.delete("mytable","_id="+event.cursor.getInt(id),null);
        }
        EventBus.getDefault().post(new UpdateHistListEvent());

        if(event.cursor.moveToFirst()){
            int fav = event.cursor.getColumnIndex("fav");
            int id = event.cursor.getColumnIndex("_id");
            if(event.cursor.getString(fav).equals("1")){
                ContentValues cv = new ContentValues();
                cv.put("del","1");
                db.update("mytable",cv,"_id="+event.cursor.getInt(id),null);
            }
            else{
                db.delete("mytable","_id="+event.cursor.getInt(id),null);
            }
            EventBus.getDefault().post(new UpdateHistListEvent());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(ClearDbEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }
        int delCount = db.delete("mytable","fav=0",null);
        Log.d("DBDeleteEvent","deleted = "+delCount+" lines");
        ContentValues cv = new ContentValues();
        cv.put("del",1);
        int updCount = db.update("mytable",cv,null,null);
        Log.d("DBDeleteEvent","update = "+updCount+" lines");
        EventBus.getDefault().post(new UpdateHistListEvent());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(FavClearEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }
        int delCount = db.delete("mytable","del=1",null);
        Log.d("DBDeleteEvent","deleted fav = "+delCount+" lines");
        ContentValues cv = new ContentValues();
        cv.put("fav","0");
        delCount = db.update("mytable",cv,"fav=1",null);
        Log.d("DBDeleteEvent","fav 1s change to 0 = "+delCount+" lines");
        EventBus.getDefault().post(new UPdateFavListEvent());
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new ClearEditTextEvent());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(DBAddEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(event.type == 1) {

            ContentValues cv = new ContentValues();
            cv.put("fromtext", event.from);
            cv.put("totext", event.to);
            cv.put("datecreate", event.date);
            cv.put("langfrom",event.fromindex);
            cv.put("langto",event.toindex);
            cv.put("fav",event.isFav);
            cv.put("del",event.isDel);
            long rowId = db.insert("mytable", null, cv);
            Cursor c = db.query("mytable",null,null,null,null,null,null);
            Log.d("DBInputEvent","added "+c.getCount());
            EventBus.getDefault().post(new UpdateHistListEvent());
        }
        else {
            int delCount = db.delete("mytable","fromtext = \""+event.from+"\"",null);
            Log.d("DBDeleteEvent","deleted = "+delCount+" lines");
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(DBUpdateEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("fav",event.isfav);
        int num = db.update("mytable",cv,"datecreate=(select datecreate from mytable order by datecreate DESC limit 1)",null);
        Log.d("Update","number update = "+num);
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new UPdateFavListEvent());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(FavAddFromHistory event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        String fav = event.c.getString(event.c.getColumnIndex("fav"));
        cv.put("fav",fav.equals("1")?"0":"1");
        db.update("mytable",cv,"_id="+event.c.getInt(event.c.getColumnIndex("_id")),null);
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new UPdateFavListEvent());
        if(fav.equals("1"))
            EventBus.getDefault().post(new FavButtonCheck(false));
        else EventBus.getDefault().post(new FavButtonCheck(true));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(FavDeleteEvent event){
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(this,"Cannot get writable DB!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(event.isDel.equals("1"))
            db.delete("mytable","_id="+event.id,null);
        else{
            ContentValues cv = new ContentValues();
            cv.put("fav","0");
            db.update("mytable",cv,"_id="+event.id,null);
        }
        EventBus.getDefault().post(new FavButtonCheck(false));
        EventBus.getDefault().post(new UPdateFavListEvent());
        EventBus.getDefault().post(new UpdateHistListEvent());
    }

}
