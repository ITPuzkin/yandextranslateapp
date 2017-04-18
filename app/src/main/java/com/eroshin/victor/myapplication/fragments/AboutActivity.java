package com.eroshin.victor.myapplication.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.eroshin.victor.myapplication.MainActivity;
import com.eroshin.victor.myapplication.R;

import org.w3c.dom.Text;

/**
 * Created by eroshin on 17.04.2017.
 */

public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt = (TextView) findViewById(R.id.about_txt);

        String about = getString(R.string.about_txt);
        Spanned aboutspanned = android.text.Html.fromHtml(about);
        txt.setText(aboutspanned);

        String appname = getString(R.string.app_name);
        final SpannableStringBuilder text = new SpannableStringBuilder(appname);
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(238, 28, 37));
        text.setSpan(style, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        toolbar.setTitle(text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
