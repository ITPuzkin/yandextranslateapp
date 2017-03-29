package com.eroshin.victor.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eroshin.victor.myapplication.R;

import java.util.HashMap;

/**
 * Created by eroshin on 28.03.2017.
 */

public class LangListAdapter2 extends BaseAdapter {

    HashMap<String,String> langs;

    LayoutInflater inflater;

    public LangListAdapter2(HashMap<String,String> map,Context ctx){
        langs = map;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return langs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.langlistitem,null,true);
            holder = new ViewHolder();
            holder.from = (TextView) view.findViewById(R.id.from);
            holder.to = (TextView) view.findViewById(R.id.to);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        holder.from.setText(langs.keySet().iterator().next());
        holder.to.setText(langs.values().iterator().next());
        return null;
    }

    class ViewHolder {
        public TextView from;
        public TextView to;
    }
}
