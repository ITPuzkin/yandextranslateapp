package com.eroshin.victor.myapplication.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.core.Translater;
import com.eroshin.victor.myapplication.events.BDEvent.DBAddEvent;
import com.eroshin.victor.myapplication.events.BDEvent.DBUpdateEvent;
import com.eroshin.victor.myapplication.events.BDEvent.FavButtonCheck;
import com.eroshin.victor.myapplication.events.TranslateEvent.GetLangsEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.GetLangsReadyEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.LangChangeEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.LangReadyEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.TranslateEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.TranslateFinishEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.TranslateStarEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.ClearEditTextEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.ProgreesBarEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eroshin on 28.03.2017.
 */

public class TranslateFragment extends Fragment {

    Translater translater;

    EditText editText1;
    TextView editText2;

    Button swapButton;
    ToggleButton favButton;

    TextView langFrom;
    TextView langTo;

    //magic
    int choosedLangFrom = 16;
    int choosedLangTo = 69;

    static String prev = "";

    private static TranslateFragment inst;

    public static TranslateFragment getInst() {
        if (inst == null)
            inst = new TranslateFragment();
        return inst;
    }

    public TranslateFragment() {

        //this.setRetainInstance(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("from", editText1.getText().toString());
        outState.putString("to", editText2.getText().toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Translatefragment", "--------Start-----" + EventBus.getDefault().isRegistered(translater));
        register();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(translater))
            EventBus.getDefault().unregister(translater);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        Log.d("Translatefragment", "--------Stop-----");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Translatefragment", "--------Destroy-----");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translater = Translater.getTranslater(getContext());
    }

    public void register() {
        if (!EventBus.getDefault().isRegistered(translater))
            EventBus.getDefault().register(translater);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.translate_fragment, container, false);

        editText1 = (EditText) root.findViewById(R.id.editText);
        editText1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "tahoma.ttf"));
        editText2 = (TextView) root.findViewById(R.id.editText2);
        editText2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "tahoma.ttf"));

        langFrom = (TextView) root.findViewById(R.id.langFrom);
        langTo = (TextView) root.findViewById(R.id.langTo);
        langFrom.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "tahoma.ttf"));
        langTo.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "tahoma.ttf"));

        langFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LangChangeEvent(true));
            }
        });

        langTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LangChangeEvent(false));
            }
        });

        editText1.addTextChangedListener(new TextWatcher() {
            Timer timer = new Timer();

            TimerTask task;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                favButton.setEnabled(false);
                favButton.setChecked(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (task != null) task.cancel();
                String txt = editText1.getText().toString();
                if (prev.equals(txt)) return;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        String txt = editText1.getText().toString();
                        if (txt.length() > 0) {
                            prev = txt;
                            EventBus.getDefault().post(new TranslateEvent(editText1.getText().toString()));
                        }
                    }
                };
                timer.schedule(task, 2000);
            }
        });

        swapButton = (Button) root.findViewById(R.id.button);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bak = choosedLangFrom;
                choosedLangFrom = choosedLangTo;
                choosedLangTo = bak;

                String txt = langFrom.getText().toString();
                langFrom.setText(langTo.getText().toString());
                langTo.setText(txt);

                if (editText1.getText().length() != 0)
                    EventBus.getDefault().post(new TranslateEvent(editText1.getText().toString()));

            }
        });

        favButton = (ToggleButton) root.findViewById(R.id.addFavorite);
        favButton.setEnabled(false);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new DBUpdateEvent(favButton.isChecked() ? "1" : "0"));
            }
        });
        favButton.setButtonDrawable(android.R.drawable.btn_star_big_off);
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Fav_checked_event", "isChecked = " + isChecked);
                if (!isChecked)
                    favButton.setButtonDrawable(android.R.drawable.btn_star_big_off);
                else
                    favButton.setButtonDrawable(android.R.drawable.btn_star_big_on);
            }
        });
        register();


        EventBus.getDefault().post(new GetLangsEvent());
        if (savedInstanceState != null && savedInstanceState.containsKey("from") && savedInstanceState.containsKey("to")) {
            editText1.setText(savedInstanceState.getString("from"));
            editText2.setText(savedInstanceState.getString("to"));
        }
        return root;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(GetLangsEvent event) {
        EventBus.getDefault().post(new ProgreesBarEvent(true));
        translater.getLangs();
        EventBus.getDefault().post(new GetLangsReadyEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(GetLangsReadyEvent event) {
        if (translater.getValues().size() != 0) {
            langTo.setText(translater.getValues().get(choosedLangTo));
            langFrom.setText(translater.getValues().get(choosedLangFrom));
        }
        EventBus.getDefault().post(new ProgreesBarEvent(false));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(TranslateEvent event) {
        EventBus.getDefault().post(new ProgreesBarEvent(true));
        String answ = translater.translate(editText1.getText().toString(), translater.getKeyFrom(choosedLangFrom), translater.getKeyTo(choosedLangTo));
        if (answ != null && answ.length() != 0)
            EventBus.getDefault().post(new TranslateFinishEvent(answ));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(TranslateFinishEvent event) {
        editText2.setText(event.getS());
        EventBus.getDefault().post(new DBAddEvent(editText1.getText().toString(), editText2.getText().toString(), System.currentTimeMillis(), 1, translater.getKeyFrom(choosedLangFrom), translater.getKeyTo(choosedLangTo), "0", "0"));
        favButton.setChecked(false);
        favButton.setEnabled(true);
        EventBus.getDefault().post(new ProgreesBarEvent(false));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(LangReadyEvent event) {
        showDialogChooser(event.isFrom);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ClearEditTextEvent event) {
        editText1.setText("");
        editText2.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(TranslateStarEvent event) {
        favButton.setChecked(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FavButtonCheck event) {
        favButton.setChecked(event.isChecked);
    }

    public void showDialogChooser(final boolean isfrom) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(view);
        builder.setTitle("List");
        ListView lw = (ListView) view.findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_activated_1, translater.getValues());
        final AlertDialog dialog = builder.create();
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isfrom) {
                    langFrom.setText((String) parent.getAdapter().getItem(position));
                    choosedLangFrom = position;
                } else {
                    langTo.setText((String) parent.getAdapter().getItem(position));
                    choosedLangTo = position;
                }
                if (editText1.getText().length() != 0)
                    EventBus.getDefault().post(new TranslateEvent(editText1.getText().toString()));
                dialog.dismiss();
            }
        });
        lw.setAdapter(adapter);

        dialog.show();
    }


}
