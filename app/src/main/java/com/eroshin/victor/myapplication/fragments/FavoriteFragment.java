package com.eroshin.victor.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.bd.FavAdapter;
import com.eroshin.victor.myapplication.events.FavClearEvent;
import com.eroshin.victor.myapplication.events.UPdateFavListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 29.03.2017.
 */

public class FavoriteFragment extends Fragment {

    RecyclerView favList;
    EditText favSearch;
    ImageButton favClear;

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
        View root = inflater.inflate(R.layout.favorite_fragment,null,false);

        favList = (RecyclerView) root.findViewById(R.id.fav_list);
        favSearch = (EditText) root.findViewById(R.id.fav_search);
        layoutManager = new LinearLayoutManager(getActivity());

        FavAdapter adapter = new FavAdapter();
        favList.setLayoutManager(layoutManager);
        favList.setAdapter(adapter);

        favClear = (ImageButton) root.findViewById(R.id.clear_fav);
        favClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FavClearEvent());
            }
        });

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(UPdateFavListEvent event){
        favList.getAdapter().notifyDataSetChanged();
    }
}
