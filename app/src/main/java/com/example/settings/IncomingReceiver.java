package com.example.settings;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncomingReceiver extends BroadcastReceiver {
    boolean isPhoneCalling;
    String missedCall="";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    Sharedpref mSharedpref;
    int i=0;
    @Override
    public void onReceive(final Context context, Intent intent) {
        mSharedpref = new Sharedpref(context);
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            Log.i("Outgoing", "RINGING, number: " );

        } else {
            final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if(lastState == state){
                        //No change, debounce extras
                        return;
                    }
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    if (TelephonyManager.CALL_STATE_RINGING == state) {
                        // phone ringing
                        Log.i("Incoming", "RINGING, number: " + incomingNumber);
                        if (i <= 0) {
                            missedCall = incomingNumber;
                            String lastentry = mSharedpref.getMissedCallNumber();
                            StringBuilder mBuilder = new StringBuilder();
                            mBuilder.append(lastentry + "," + "{ Date: " + currentDate + ", Time: " + currentTime + ", Number: " + missedCall + "}");
                            mSharedpref.setMissedCallNumber(mBuilder.toString());
                            mSharedpref.commit();
                            i++;
                        }
                    }

                    if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                        // active
                        Log.i("Incoming", "OFFHOOK");
                        if(lastState == TelephonyManager.CALL_STATE_RINGING){
                            isPhoneCalling = true;
                        }
                    }

                    if (TelephonyManager.CALL_STATE_IDLE == state) {
                        // run when class initial and phone call ended, need detect flag
                        // from CALL_STATE_OFFHOOK
                        Log.i("Incoming", "IDLE number");

                        if (isPhoneCalling) {

                            Handler handler = new Handler();

                            //Put in delay because call log is not updated immediately when state changed
                            // The dialler takes a little bit of time to write to it 500ms seems to be enough
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    // get start of cursor
                                    Log.i("CallLogDetailsActivity", "Getting Log activity...");
                                    String[] projection = new String[]{CallLog.Calls.NUMBER};
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    } else {
                                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " desc");
                                        managedCursor.moveToFirst();
                                        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                                        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                                        String lastCallnumber = managedCursor.getString(number);
                                        String lastCallTime = managedCursor.getString(duration);
                                        String lastentry = mSharedpref.getRecievedNumbers();
                                        StringBuilder mBuilder = new StringBuilder();
                                        mBuilder.append(lastentry + "," + "{ Date: " + currentDate + ", Time: " + currentTime + ",Number: " + lastCallnumber + ", Duration: " + lastCallTime + "sec }");
                                        mSharedpref.setRecievedNumbers(mBuilder.toString());
                                        mSharedpref.commit();
                                        System.out.println("lastCallnumber : " + lastCallnumber + lastCallTime);
                                    }
                                }
                            }, 500);

                            isPhoneCalling = false;
                        }

                    }
                    System.out.println("incomingNumber : " + incomingNumber);
                    lastState = state;
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);

        }
    }
}
