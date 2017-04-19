package com.eroshin.victor.myapplication.fragments;

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
import com.eroshin.victor.myapplication.bd.AutoCompleteAdapter;
import com.eroshin.victor.myapplication.bd.FavAdapter;
import com.eroshin.victor.myapplication.events.BDEvent.FavClearEvent;
import com.eroshin.victor.myapplication.events.BDEvent.GetPosEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.ScrollToEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.UPdateFavListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by eroshin on 29.03.2017.
 */

public class FavoriteFragment extends Fragment {

    RecyclerView favList;
    AutoCompleteTextView favSearch;
    ImageButton favClear;
    FavAdapter adapter;

    RecyclerView.LayoutManager layoutManager;

    AutoCompleteAdapter myAdapter;
    ArrayAdapter<String> myArrayAdapter;

    private static FavoriteFragment inst;

    public static FavoriteFragment getInst() {
        if (inst == null)
            inst = new FavoriteFragment();
        return inst;
    }

    public FavoriteFragment() {
        this.setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (EventBus.getDefault().isRegistered(adapter))
            EventBus.getDefault().unregister(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (!EventBus.getDefault().isRegistered(adapter))
            EventBus.getDefault().register(adapter);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.favorite_fragment, null, false);

        favList = (RecyclerView) root.findViewById(R.id.fav_list);
        favSearch = (AutoCompleteTextView) root.findViewById(R.id.fav_search);
        layoutManager = new LinearLayoutManager(getActivity());

        adapter = new FavAdapter(getContext());
        favList.setLayoutManager(layoutManager);
        favList.setAdapter(adapter);

        favClear = (ImageButton) root.findViewById(R.id.clear_fav);
        favClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, getString(R.string.deleteSnack), Snackbar.LENGTH_LONG).setAction(getString(R.string.deleteSnackBtn), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FavClearEvent());
                        favSearch.setText("");
                    }
                }).setActionTextColor(getResources().getColor(R.color.myColorRed)).show();
            }
        });
        favClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new FavClearEvent());
                favSearch.setText("");
                return true;
            }
        });

        myAdapter = new AutoCompleteAdapter(getContext(), 0, MainActivity.dbHelper);
        myAdapter.init("fav=1");
        myArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, myAdapter.getList());

        favSearch.setAdapter(myArrayAdapter);
        favSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String str = textView.getText().toString();
                EventBus.getDefault().post(new GetPosEvent(str));
            }
        });

        return root;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(GetPosEvent event) {
        if (event.fav) {
            FavAdapter adapter = (FavAdapter) favList.getAdapter();
            int pos = adapter.getPosition(event.str);
            EventBus.getDefault().post(new ScrollToEvent(pos));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ScrollToEvent event) {
        if (event.fav) {
            Log.d("ScrollEvent", "scrolled to " + event.position);
            favList.scrollToPosition(event.position);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(UPdateFavListEvent event) {
        favList.getAdapter().notifyDataSetChanged();
        myAdapter.init("fav=1");
        myArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, myAdapter.getList());
        favSearch.setAdapter(myArrayAdapter);
        myArrayAdapter.notifyDataSetChanged();
    }
}
