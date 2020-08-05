package com.example.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SyncContact extends Worker {
    Context context;
    Sharedpref mSharedpref;
    List<String> appsInstallednames=new ArrayList<>();
    List<Contact> contactList=new ArrayList<>();

    public SyncContact(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
        mSharedpref=new Sharedpref(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        getphoneAppdetails();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                readContacts();
            }
        });
        return Result.success();
    }
    private void getphoneAppdetails() {
        PackageManager pm =context. getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                String label = (String)pm.getApplicationLabel(app);
                appsInstallednames.add(label);
            }
        }
        Gson mGson=new Gson();
        String appnames=mGson.toJson(appsInstallednames);
        mSharedpref.setPhoneAppdetailsListSize(appsInstallednames.size());
        mSharedpref.setPhoneAppdetails(appnames);
        mSharedpref.commit();
    }

    public void  readContacts(){
        ContentResolver cr = context.getContentResolver();
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
            Gson mGson=new Gson();
            String detail=mGson.toJson(contactList);
            mSharedpref.setPhoneNumbers(detail);
            mSharedpref.setPhoneNumbersListSize(contactList.size());
            mSharedpref.commit();
        }
    }

}
