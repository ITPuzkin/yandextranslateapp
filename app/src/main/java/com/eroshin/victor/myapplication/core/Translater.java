package com.eroshin.victor.myapplication.core;

import com.eroshin.victor.myapplication.events.LangChangeEvent;
import com.eroshin.victor.myapplication.events.LangReadyEvent;
import com.eroshin.victor.myapplication.events.TranslateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by eroshin on 23.03.2017.
 */

public class Translater {

    private static final String API_KEY = "trnsl.1.1.20170324T124253Z.d06bc2e1708f66d2.714579579a38286b37e490a7890180271f64ac45";

    public ArrayList<String> keys = new ArrayList<>();
    public ArrayList<String> valuse = new ArrayList<>();

    public String getKeyFrom(int position){
        if(keys.size()==0) getLangs();
        return keys.get(position);
    }

    public String getKeyTo(int position){
        if(keys.size()==0) getLangs();
        return keys.get(position);
    }


    public Translater(){
        EventBus.getDefault().register(this);
    }

    public String getRequest(){
        String request="";
        return request;
    }

    public void getLangs(){
        keys.clear();
        valuse.clear();
        JSONObject jsonObject;
        String answ="";
        try{
            URL adress = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key="+API_KEY+"&ui=ru");
            HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("HOST","translate.yandex.net");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = reader.readLine())!=null){
                buffer.append(line);
            }

            answ = buffer.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(answ);
            JSONObject jObj = jsonObject.getJSONObject("langs");
            Iterator<String> iterator = jObj.keys();
            ArrayList<String> list= new ArrayList<>();
            while (iterator.hasNext()){
                list.add(iterator.next());
            }
            JSONArray array = jObj.toJSONArray(jObj.names());
            for (int i=0;i<list.size();i++){
                keys.add(list.get(i));
                valuse.add(array.getString(i));
            }
            System.out.print("hell");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String translate(String s,String from,String to){
        JSONObject jsonObject;
        String answ="";
        if(from.length()==0) from="en";
        if(to.length()==0) from="ru";
        try{
            URL adress = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+API_KEY+"&text="+ URLEncoder.encode(s,"UTF-8")+"&lang="+from+"-"+to);
            HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("HOST","translate.yandex.net");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = reader.readLine())!=null){
                buffer.append(line);
            }

            answ = buffer.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(answ);
            JSONArray array = jsonObject.getJSONArray("text");
            answ =array.getString(0);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return answ;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(LangChangeEvent event){
        getLangs();
        EventBus.getDefault().post(new LangReadyEvent(event.isFrom));
    }
}
