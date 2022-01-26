package com.example.settings;

import static com.example.settings.MainActivity.setuponetimeworkManager;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {

    Sharedpref mSharedpref;
    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        mSharedpref = new Sharedpref(getApplicationContext());

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }

        String title ="",text="";
        String prevnot=mSharedpref.getServiceNotificationData();
        JSONArray mJsonArray;
        try {
            if (StringUtils.isBlank(prevnot)){
                mJsonArray= new JSONArray();
            }
            else {
                mJsonArray= new JSONArray(prevnot);
            }
            JSONObject mJsonObject=new JSONObject();
            Bundle extras = sbn.getNotification().extras;
            title = extras.getString("android.title");
            text = extras.getCharSequence("android.text").toString();
            int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap id = sbn.getNotification().largeIcon;
            Log.i("Package",pack);
            Log.i("Ticker",ticker);
            Log.i("Title",title);
            Log.i("Text",text);
            mJsonObject.put("package Name",pack);
            mJsonObject.put("title",title);
            mJsonObject.put("text",text);
            mJsonObject.put("ticker",ticker);
            mJsonArray.put(mJsonObject);
            mSharedpref.setServicerNotificationData(mJsonArray.toString());
            mSharedpref.commit();

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            if(id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                msgrcv.putExtra("icon",byteArray);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            }
            catch (Exception e){
                e.printStackTrace();
            }

    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}