package com.eroshin.victor.myapplication.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eroshin.victor.myapplication.MainActivity;
import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.bd.HistoryAdapter;
import com.eroshin.victor.myapplication.events.ClearDbEvent;
import com.eroshin.victor.myapplication.events.ClearEditTextEvent;
import com.eroshin.victor.myapplication.events.GetPosEvent;
import com.eroshin.victor.myapplication.events.ScrollToEvent;
import com.eroshin.victor.myapplication.events.UpdateHistListEvent;
import com.eroshin.victor.myapplication.view.AutoCompleteAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 29.03.2017.
 */

public class HistoryFragment extends Fragment {

    AutoCompleteTextView histSearch;
    ImageButton histClear;
    RecyclerView histList;

    AutoCompleteAdapter myAdapter;

    RecyclerView.LayoutManager layoutManager;

    ArrayAdapter<String> myArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.history_fragment,null,false);

        histSearch = (AutoCompleteTextView) root.findViewById(R.id.hist_search);
        histClear = (ImageButton) root.findViewById(R.id.hist_clear);
        histList = (RecyclerView) root.findViewById(R.id.hist_list);

        layoutManager = new LinearLayoutManager(getActivity());
        histList.setLayoutManager(layoutManager);
        HistoryAdapter adapter = new HistoryAdapter(getContext());
        histList.setAdapter(adapter);
        histClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,getString(R.string.deleteSnack),Snackbar.LENGTH_LONG).setAction(getString(R.string.deleteSnackBtn), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new ClearDbEvent());
                        EventBus.getDefault().post(new ClearEditTextEvent());
                    }
                }).setActionTextColor(getResources().getColor(R.color.myColorRed)).show();
            }
        });

        histClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new ClearDbEvent());
                EventBus.getDefault().post(new ClearEditTextEvent());
                return true;
            }
        });

        myAdapter = new AutoCompleteAdapter(getContext(),0, MainActivity.dbHelper);
        myAdapter.init("del=0");

        myArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,myAdapter.strings);
        histSearch.setAdapter(myArrayAdapter);
        histSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String str = textView.getText().toString();
                Log.d("AutoComplete","selected "+position+" id="+id+" txt="+str);
                EventBus.getDefault().post(new GetPosEvent(str));
            }
        });
        histSearch.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"tahoma.ttf"));

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(UpdateHistListEvent event){
        histList.getAdapter().notifyDataSetChanged();
        myAdapter.init("del=0");
        myArrayAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(GetPosEvent event){
        HistoryAdapter adapter = (HistoryAdapter)histList.getAdapter();
        int pos = adapter.getPosition(event.str);
        EventBus.getDefault().post(new ScrollToEvent(pos));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ScrollToEvent event){
        Log.d("ScrollEvent","scrolled to "+event.position);
        histList.scrollToPosition(event.position);
    }

}
