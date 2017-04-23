package com.eroshin.victor.myapplication.core;

import android.content.Context;
import android.util.Log;

import com.eroshin.victor.myapplication.R;
import com.eroshin.victor.myapplication.events.NoConnectEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.LangChangeEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent.LangReadyEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.CheckButtonEvent;
import com.eroshin.victor.myapplication.events.ViewEvent.SnackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by eroshin on 23.03.2017.
 */

public class Translater {

    private static final String API_KEY = "trnsl.1.1.20170324T124253Z.d06bc2e1708f66d2.714579579a38286b37e490a7890180271f64ac45";

    private static final int SECONDS = 6;

    private static Translater translater;
    private Context ctx;

    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();

    public ArrayList<String> getValues() {
        return values;
    }
    public ArrayList<String> getKeys() {
        return keys;
    }

    public String getKeyFrom(int position) {
        if (keys.size() == 0) getLangs();
        return keys.get(position);
    }


    private Translater(Context c) {
        ctx = c;
        //EventBus.getDefault().register(this);
    }

    public static Translater getTranslater(Context c) {
        if (translater == null)
            translater = new Translater(c);
        return translater;
    }

    public String getRequest() {
        String request = "";
        return request;
    }

    public void getLangs() {
        if (!keys.isEmpty() && !values.isEmpty())
            return;
        keys.clear();
        values.clear();
        JSONObject jsonObject;
        String answ = "";
        HttpURLConnection connection = null;
        try {
            URL adress = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + API_KEY + "&ui=" + Locale.getDefault().getLanguage());
            connection = (HttpURLConnection) adress.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("HOST", "translate.yandex.net");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(SECONDS * 1000);
            connection.connect();

            InputStream input = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            answ = buffer.toString();

            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Translater", "getLangs no connection");
            EventBus.getDefault().post(new NoConnectEvent());
            return;
        } finally {
            if(connection!=null) connection.disconnect();
        }

        try {
            jsonObject = new JSONObject(answ);
            JSONObject jObj = jsonObject.getJSONObject("langs");
            Iterator<String> iterator = jObj.keys();
            ArrayList<String> list = new ArrayList<>();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            JSONArray array = jObj.toJSONArray(jObj.names());
            for (int i = 0; i < list.size(); i++) {
                keys.add(list.get(i));
                values.add(array.getString(i));
            }
            EventBus.getDefault().post(new CheckButtonEvent(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String translate(String s, String from, String to) {
        JSONObject jsonObject;
        String answ = "";
        if (from.length() == 0) from = "en";
        if (to.length() == 0) from = "ru";HttpURLConnection connection = null;
        try {
            if (s.length() == 0) throw new Exception();
            URL adress = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + API_KEY + "&text=" + URLEncoder.encode(s, "UTF-8") + "&lang=" + from + "-" + to);
            connection = (HttpURLConnection) adress.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("HOST", "translate.yandex.net");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(SECONDS * 1000);
            connection.connect();

            InputStream input = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            answ = buffer.toString();
            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new NoConnectEvent());
            return null;
        }
        finally {
            if(connection!=null) connection.disconnect();
        }


        try {
            jsonObject = new JSONObject(answ);
            String code = jsonObject.getString("code");
            JSONArray array = jsonObject.getJSONArray("text");
            answ = array.getString(0);
            codeCheck(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new CheckButtonEvent(false));
        return answ;
    }

    public void codeCheck(String code){
        if(code.equals("401"))
            Log.e("Translater","Неправильный API-ключ");
        else if(code.equals("402"))
            Log.e("Translater","API-ключ заблокирован");
        else if(code.equals("404")) {
            Log.e("Translater", "Превышено суточное ограничение на объем переведенного текста");
            EventBus.getDefault().post(new SnackEvent(ctx.getResources().getString(R.string.err_limit)));
        }
        else if(code.equals("413")){
            Log.e("Translater", "Превышен максимально допустимый размер текста");
            EventBus.getDefault().post(new SnackEvent(ctx.getResources().getString(R.string.err_txtlimit)));
        }
        else if(code.equals("422")){
            Log.e("Translater", "Текст не может быть переведен");
            EventBus.getDefault().post(new SnackEvent(ctx.getResources().getString(R.string.err_txtcnt)));
        }
        else if(code.equals("501")){
            Log.e("Translater", "Заданное направление перевода не поддерживается");
            EventBus.getDefault().post(new SnackEvent(ctx.getResources().getString(R.string.err_txtdirect)));
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessage(LangChangeEvent event) {
        getLangs();
        EventBus.getDefault().post(new LangReadyEvent(event.isFrom));
    }
}
