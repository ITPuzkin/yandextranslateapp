package com.eroshin.victor.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
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

public class LangListAdapter extends RecyclerView.Adapter<LangListAdapter.ViewHolder> {

    HashMap<String,String> langs;

    public LangListAdapter(HashMap<String,String> map){
        langs = map;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.langlistitem,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return langs.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.from.setText(langs.keySet().iterator().next());
        holder.to.setText(langs.values().iterator().next());
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from;
        public TextView to;

        public ViewHolder(View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.from);
            to = (TextView) itemView.findViewById(R.id.to);
        }
    }
}
