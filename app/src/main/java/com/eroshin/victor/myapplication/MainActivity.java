package com.eroshin.victor.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eroshin.victor.myapplication.bd.DBHelper;
import com.eroshin.victor.myapplication.bd.ViewPagerAdapter;
import com.eroshin.victor.myapplication.events.BDEvent.ClearDbEvent;
import com.eroshin.victor.myapplication.events.BDEvent.DBAddEvent;
import com.eroshin.victor.myapplication.events.BDEvent.DBUpdateEvent;
import com.eroshin.victor.myapplication.events.BDEvent.DellHistEvent;
import com.eroshin.victor.myapplication.events.BDEvent.FavAddFromHistory;
import com.eroshin.victor.myapplication.events.BDEvent.FavButtonCheck;
import com.eroshin.victor.myapplication.events.BDEvent.FavClearEvent;
import com.eroshin.victor.myapplication.events.BDEvent.FavDeleteEvent;
import com.eroshin.victor.myapplication.events.NoConnectEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.TranslateEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.CheckButtonEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.ClearEditTextEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.ProgreesBarEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.SnackEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.UPdateFavListEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.UpdateHistListEvent;
import com.eroshin.victor.myapplication.fragments.AboutActivity;
import com.eroshin.victor.myapplication.fragments.FavoriteFragment;
import com.eroshin.victor.myapplication.fragments.HistoryFragment;
import com.eroshin.victor.myapplication.fragments.TranslateFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 28.03.2017.
 */

public class MainActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager pager;
    Toolbar toolbar;

    ProgressBar bar;
    Button chkBtn;
    public static DBHelper dbHelper;

    TranslateFragment translateFragment = null;
    HistoryFragment historyFragment = null;
    FavoriteFragment favoriteFragment = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent(this, AboutActivity.class);
        startActivity(it);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        Log.d("MainActivity", "-- on create mainActivity");
        setContentView(R.layout.mainactivity);
        tabs = (TabLayout) findViewById(R.id.tabLayout);
        pager = (ViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        String appname = getString(R.string.app_name);
        final SpannableStringBuilder text = new SpannableStringBuilder(appname);
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(238, 28, 37));
        text.setSpan(style, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        toolbar.setTitle(text);

        setupViewPager(pager);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        tabs.setupWithViewPager(pager);

        final int[] imageResId = {
                R.drawable.translate_ico,
                R.drawable.history_ico,
                R.drawable.fav_ico,

        };

        final int[] imageResIdSer = {
                R.drawable.translate_ser_ico,
                R.drawable.history_ser_ico,
                R.drawable.fav_ser_ico,
        };

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabid = tab.getPosition();
                switch (tabid) {
                    case 0:
                        tab.setIcon(imageResId[0]);
                        return;
                    case 1:
                        tab.setIcon(imageResId[1]);
                        return;
                    case 2:
                        tab.setIcon(imageResId[2]);
                        return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabid = tab.getPosition();
                switch (tabid) {
                    case 0:
                        tab.setIcon(imageResIdSer[0]);
                        return;
                    case 1:
                        tab.setIcon(imageResIdSer[1]);
                        return;
                    case 2:
                        tab.setIcon(imageResIdSer[2]);
                        return;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//TODO: change!!
        for (int i = 0; i < tabs.getTabCount(); i++)
            tabs.getTabAt(i).setIcon(imageResIdSer[i]);
        tabs.getTabAt(0).setIcon(imageResId[0]);

        bar = (ProgressBar) findViewById(R.id.progressBar);
        chkBtn = (Button) findViewById(R.id.chk_btn);
        chkBtn.setVisibility(View.GONE);
        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TranslateEvent());
            }
        });

        dbHelper = new DBHelper(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "-- on resume mainActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "-- on start mainActivity");
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        if (translateFragment == null) translateFragment = TranslateFragment.getInst();
        if (historyFragment == null) historyFragment = HistoryFragment.getInst();
        if (favoriteFragment == null) favoriteFragment = FavoriteFragment.getInst();
        adapter.addFragment(translateFragment, getString(R.string.tab1_translate));
        adapter.addFragment(historyFragment, getString(R.string.tab3_history));
        adapter.addFragment(favoriteFragment, getString(R.string.tab2_favorites));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    public SQLiteDatabase getDB() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.dbError), Toast.LENGTH_SHORT).show();
        }
        return db;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ProgreesBarEvent event) {
        bar.setVisibility(event.isVisible ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DellHistEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;

        if (event.isFav.equals("1")) {
            ContentValues cv = new ContentValues();
            cv.put("del", "1");
            db.update(DBHelper.TABLE_NAME, cv, "_id=" + event.id, null);
        } else {
            db.delete(DBHelper.TABLE_NAME, "_id=" + event.id, null);
        }
        EventBus.getDefault().post(new UpdateHistListEvent());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ClearDbEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;
        int delCount = db.delete(DBHelper.TABLE_NAME, "fav=0", null);
        Log.d("DBDeleteEvent", "deleted = " + delCount + " lines");
        ContentValues cv = new ContentValues();
        cv.put("del", 1);
        int updCount = db.update(DBHelper.TABLE_NAME, cv, null, null);
        Log.d("DBDeleteEvent", "update = " + updCount + " lines");
        EventBus.getDefault().post(new UpdateHistListEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FavClearEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;
        int delCount = db.delete(DBHelper.TABLE_NAME, "del=1", null);
        Log.d("DBDeleteEvent", "deleted fav = " + delCount + " lines");
        ContentValues cv = new ContentValues();
        cv.put("fav", "0");
        delCount = db.update(DBHelper.TABLE_NAME, cv, "fav=1", null);
        Log.d("DBDeleteEvent", "fav 1s change to 0 = " + delCount + " lines");
        EventBus.getDefault().post(new UPdateFavListEvent());
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new ClearEditTextEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DBAddEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;

        if (event.type == 1) {

            ContentValues cv = new ContentValues();
            cv.put("fromtext", event.from);
            cv.put("totext", event.to);
            cv.put("datecreate", event.date);
            cv.put("langfrom", event.fromindex);
            cv.put("langto", event.toindex);
            cv.put("fav", event.isFav);
            cv.put("del", event.isDel);
            db.insert(DBHelper.TABLE_NAME, null, cv);
            Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
            Log.d("DBInputEvent", "added " + c.getCount());
            EventBus.getDefault().post(new UpdateHistListEvent());
            c.close();
        } else {
            int delCount = db.delete(DBHelper.TABLE_NAME, "fromtext = \"" + event.from + "\"", null);
            Log.d("DBDeleteEvent", "deleted = " + delCount + " lines");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DBUpdateEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;

        ContentValues cv = new ContentValues();
        cv.put("fav", event.isfav);
        int num = db.update(DBHelper.TABLE_NAME, cv, "datecreate=(select datecreate from mytable order by datecreate DESC limit 1)", null);
        Log.d("Update", "number update = " + num);
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new UPdateFavListEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FavAddFromHistory event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;

        ContentValues cv = new ContentValues();
        String fav = event.isFav;
        cv.put("fav", fav.equals("1") ? "0" : "1");
        db.update(DBHelper.TABLE_NAME, cv, "_id=" + event.id, null);
        EventBus.getDefault().post(new UpdateHistListEvent());
        EventBus.getDefault().post(new UPdateFavListEvent());
        if (fav.equals("1"))
            EventBus.getDefault().post(new FavButtonCheck(false));
        else EventBus.getDefault().post(new FavButtonCheck(true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FavDeleteEvent event) {
        SQLiteDatabase db = getDB();
        if (db == null) return;

        if (event.isDel.equals("1"))
            db.delete(DBHelper.TABLE_NAME, "_id=" + event.id, null);
        else {
            ContentValues cv = new ContentValues();
            cv.put("fav", "0");
            db.update(DBHelper.TABLE_NAME, cv, "_id=" + event.id, null);
        }
        EventBus.getDefault().post(new FavButtonCheck(false));
        EventBus.getDefault().post(new UPdateFavListEvent());
        EventBus.getDefault().post(new UpdateHistListEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(NoConnectEvent event) {
        Snackbar.make(pager, getString(R.string.checkConnection), Snackbar.LENGTH_SHORT).show();
        EventBus.getDefault().post(new ProgreesBarEvent(false));
        EventBus.getDefault().post(new CheckButtonEvent(true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(CheckButtonEvent event) {
        chkBtn.setVisibility(event.isVisible ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(SnackEvent event){
        Snackbar.make(pager,event.msg,Snackbar.LENGTH_SHORT).show();
    }

}
