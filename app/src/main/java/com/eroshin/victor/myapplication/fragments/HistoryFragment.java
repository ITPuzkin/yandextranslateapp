package com.eroshin.victor.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eroshin.victor.myapplication.MainActivity;
import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.bd.FavAdapter;
import com.eroshin.victor.myapplication.bd.HistoryAdapter;
import com.eroshin.victor.myapplication.events.ClearDbEvent;
import com.eroshin.victor.myapplication.events.ClearEditTextEvent;
import com.eroshin.victor.myapplication.events.GetPosEvent;
import com.eroshin.victor.myapplication.events.ScrollTo;
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
        HistoryAdapter adapter = new HistoryAdapter();
        histList.setAdapter(adapter);
        histClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ClearDbEvent());
                EventBus.getDefault().post(new ClearEditTextEvent());
            }
        });

        myAdapter = new AutoCompleteAdapter(getContext(),0, MainActivity.dbHelper);
        myAdapter.init();

        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,myAdapter.strings);
        histSearch.setAdapter(aa);
        histSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String str = textView.getText().toString();
                Log.d("AutoComplete","selected "+position+" id="+id+" txt="+str);
                EventBus.getDefault().post(new GetPosEvent(str));
            }
        });

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(UpdateHistListEvent event){
        histList.getAdapter().notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(GetPosEvent event){
        HistoryAdapter adapter = (HistoryAdapter)histList.getAdapter();
        int pos = adapter.getPosition(event.str);
        EventBus.getDefault().post(new ScrollTo(pos));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ScrollTo event){
        Log.d("ScrollEvent","scrolled to "+event.position);
        histList.scrollToPosition(event.position);
    }

}
