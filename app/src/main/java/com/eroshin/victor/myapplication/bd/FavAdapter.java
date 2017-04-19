package com.eroshin.victor.myapplication.bd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eroshin.victor.myapplication.MainActivity;
import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.events.BDEvent.FavDeleteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 29.03.2017.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private SQLiteDatabase db;

    Typeface TAHOMA;
    Typeface TAHOMABD;

    public int getPosition(String str) {
        if (db != null) {
            db = MainActivity.dbHelper.getWritableDatabase();
            Cursor c = db.query(DBHelper.TABLE_NAME, null, "fav=1", null, null, null, "datecreate desc");
            if (c.moveToFirst()) {
                int txt = c.getColumnIndex("fromtext");
                int txtto = c.getColumnIndex("totext");
                do {
                    if (c.getString(txt).equals(str) || c.getString(txtto).equals(str))
                        return c.getPosition();
                } while (c.moveToNext());
            }
            c.close();
        }
        return 0;
    }

    public FavAdapter(Context c) {
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new GetDBEvent());
        TAHOMA = Typeface.createFromAsset(c.getAssets(), "tahoma.ttf");
        TAHOMABD = Typeface.createFromAsset(c.getAssets(), "tahomabd.ttf");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (db != null) {
            final Cursor c = db.query(DBHelper.TABLE_NAME, null, "fav=1", null, null, null, "datecreate desc");
            if (c.moveToFirst()) {
                final int idindex = c.getColumnIndex("_id");
                int fromIndex = c.getColumnIndex("fromtext");
                int toIndex = c.getColumnIndex("totext");
                int langfrom = c.getColumnIndex("langfrom");
                int langto = c.getColumnIndex("langto");

                //final int favid = c.getColumnIndex("fav");
                final int delid = c.getColumnIndex("del");

                if (c.moveToPosition(position)) {
                    holder.from.setText(c.getString(fromIndex));
                    holder.to.setText(c.getString(toIndex));
                    holder.fromto.setText(c.getString(langfrom) + "-" + c.getString(langto));
                    holder.favImg.setImageResource(android.R.drawable.btn_star_big_on);
                    holder.favImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new FavDeleteEvent(c.getInt(idindex), c.getString(delid)));
                        }
                    });
                    holder.favDel.setVisibility(View.INVISIBLE);

                    holder.from.setTypeface(TAHOMABD);
                    holder.to.setTypeface(TAHOMA);
                    holder.fromto.setTypeface(TAHOMA);
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        if (db != null) {
            db = MainActivity.dbHelper.getReadableDatabase();
            return db.query(DBHelper.TABLE_NAME, null, "fav=1", null, null, null, "datecreate desc").getCount();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from;
        private TextView to;
        private TextView fromto;

        private ImageView favImg;
        private ImageButton favDel;

        private ViewHolder(View v) {
            super(v);
            from = (TextView) v.findViewById(R.id.fav_from);
            to = (TextView) v.findViewById(R.id.fav_to);
            fromto = (TextView) v.findViewById(R.id.fav_fromto);
            favImg = (ImageView) v.findViewById(R.id.fav_img);
            favDel = (ImageButton) v.findViewById(R.id.del_fav);
        }
    }

    static class GetDBEvent {
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(GetDBEvent event) {
        db = MainActivity.dbHelper.getWritableDatabase();
    }
}
