package com.example.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Sharedpref mSharedpref;
    List<Contact> contactList=new ArrayList<>();
    EditText mEditname;
    Button save,Setting;
    private PeriodicWorkRequest mPeriodicWorkRequest;
    private WorkManager mWorkManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedpref=new Sharedpref(this);
        String lastentry=mSharedpref.getOutgoingNumbers();
        String lastentry1=mSharedpref.getMissedCallNumber();
        String lastentry2=mSharedpref.getRecievedNumbers();
        mEditname=findViewById(R.id.ed_name);
        save=findViewById(R.id.button);
        Setting=findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditname.getText().toString().isEmpty()){
                    mSharedpref.setSaveName(mEditname.getText().toString());
                    mSharedpref.commit();
                    mEditname.setEnabled(false);
                    save.setEnabled(false);
                    save.setOnClickListener(null);
                }
            }
        });
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 100);
            }
        });
        if(!mSharedpref.getSaveName().isEmpty()){
            mEditname.setText(mSharedpref.getSaveName());
            mEditname.setEnabled(false);
            save.setEnabled(false);
            save.setOnClickListener(null);
        }
        Log.d("Outgoing Numbers",lastentry2);
        checkpermissions();
        setupWorkManager();
    }

    private void setupWorkManager() {
        mWorkManager=WorkManager.getInstance();
        mPeriodicWorkRequest=new PeriodicWorkRequest.Builder(SyncData.class,1,
                TimeUnit.HOURS).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS).build();
        mWorkManager.enqueueUniquePeriodicWork("PERIODIC_REQUEST_TAG", ExistingPeriodicWorkPolicy.KEEP,mPeriodicWorkRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            setupWorkManager();
        }
    }

    private void checkpermissions() {
      try {
          if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                  != PackageManager.PERMISSION_GRANTED) &&
                  (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                          != PackageManager.PERMISSION_GRANTED)
                  &&  (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)
                  != PackageManager.PERMISSION_GRANTED)
                  &&  (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
                  != PackageManager.PERMISSION_GRANTED)
                  &&  (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS)
                  != PackageManager.PERMISSION_GRANTED))

          {
              ActivityCompat.requestPermissions(this,
                      new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS,
                              Manifest.permission.READ_PHONE_STATE,
                              Manifest.permission.ACCESS_COARSE_LOCATION,
                              Manifest.permission.READ_CALL_LOG,
                              Manifest.permission.READ_CONTACTS,
                              Manifest.permission.WRITE_CONTACTS
                      },
                      1);
          }
          else {
              AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                      readContacts();
                  }
              });
          }
          }
      catch (Exception e){
          e.printStackTrace();
      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                readContacts();
            }
        });
    }

    public void readContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                StringBuilder mPhoneBuilder=new StringBuilder();
                StringBuilder emailBuilder=new StringBuilder();
                StringBuilder noteBuilder=new StringBuilder();
                StringBuilder addressBuilder=new StringBuilder();
                StringBuilder mInstantMessenger=new StringBuilder();
                StringBuilder Organizations=new StringBuilder();

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact mContact=new Contact();
                mContact.setId(id);
                mContact.setName(name);
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);


                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println("phone" + phone);
                        mPhoneBuilder.append(phone+",");
                    }
                    pCur.close();
                    mContact.setPhoneNumber(mPhoneBuilder.toString());
                    System.out.println("phone" + mPhoneBuilder.toString());


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
                        emailBuilder.append("Email " + email + " Email Type : " + emailType);
                        System.out.println("Email " + email + " Email Type : " + emailType);
                    }
                    emailCur.close();
                    mContact.setEmail(emailBuilder.toString());

                    // Get note.......
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
                }
                contactList.add(mContact);
            }
            mSharedpref.setPhoneNumbersList(String.valueOf(contactList.size()));
            mSharedpref.commit();
        }
    }


}