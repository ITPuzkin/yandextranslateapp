package com.eroshin.victor.myapplication.bd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eroshin.victor.myapplication.MainActivity;
import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.events.DellHistEvent;
import com.eroshin.victor.myapplication.events.FavAddFromHistory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 29.03.2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    SQLiteDatabase db;

    public HistoryAdapter(){
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new FavAdapter.GetDBEvent());
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public int getItemCount() {
        if(db!=null) {
            db = MainActivity.dbHelper.getWritableDatabase();
            return db.query("mytable", null, "del=0", null, null, null, null).getCount();
        }
        return 0;
    }

    public int getPosition(String str){
        if(db!=null) {
            db = MainActivity.dbHelper.getWritableDatabase();
            Cursor c = db.query("mytable", null, "del=0", null, null, null, "datecreate desc");
            if(c.moveToFirst()){
                int txt = c.getColumnIndex("fromtext");
                do{
                    if(c.getString(txt).equals(str))
                        return c.getPosition();
                }while (c.moveToNext());
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(db != null) {
            final Cursor c = db.query("mytable", null, "del=0",null, null, null, "datecreate desc");
            if (c.moveToFirst()) {
                int fromIndex = c.getColumnIndex("fromtext");
                int toIndex = c.getColumnIndex("totext");
                int langfrom = c.getColumnIndex("langfrom");
                int langto = c.getColumnIndex("langto");
                int dateind = c.getColumnIndex("datecreate");
                int favid = c.getColumnIndex("fav");

                final int idindex = c.getColumnIndex("_id");
                final int delid = c.getColumnIndex("del");

                if(c.moveToPosition(position)) {
                    holder.position = position;
                    holder.from.setText(c.getString(fromIndex));
                    holder.to.setText(c.getString(toIndex));
                    holder.fromto.setText(c.getString(langfrom)+"-"+c.getString(langto));
                    holder.histImg.setImageResource(c.getString(favid).equals("1")?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
                    holder.histImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new FavAddFromHistory(c.getInt(idindex),c.getString(delid)));
                        }
                    });
                    holder.histDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new DellHistEvent(c.getInt(idindex),c.getString(delid)));
                        }
                    });
                }
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView from;
        public TextView to;
        public TextView fromto;

        public ImageView histImg;
        public ImageButton histDel;

        public int position;

        public ViewHolder(View v){
            super(v);
            from = (TextView)v.findViewById(R.id.fav_from);
            to = (TextView)v.findViewById(R.id.fav_to);
            fromto = (TextView)v.findViewById(R.id.fav_fromto);
            histImg = (ImageView) v.findViewById(R.id.fav_img);
            histDel = (ImageButton) v.findViewById(R.id.del_fav);
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(FavAdapter.GetDBEvent event){
        db = MainActivity.dbHelper.getWritableDatabase();
    }

}
