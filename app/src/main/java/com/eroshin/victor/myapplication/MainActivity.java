package com.eroshin.victor.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eroshin.victor.myapplication.fragments.TranslateFragment;
import com.eroshin.victor.myapplication.fragments.testFragment;

/**
 * Created by eroshin on 28.03.2017.
 */

public class MainActivity extends AppCompatActivity{

    TabLayout tabs;
    ViewPager pager;
    Toolbar toolbar;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TranslateFragment(), getString(R.string.tab1_translate));
        adapter.addFragment(new testFragment(),getString(R.string.tab2_favorites));
        adapter.addFragment(new testFragment(),getString(R.string.tab3_history));
        viewPager.setAdapter(adapter);
    }

}
