package com.example.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Sharedpref {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "Rozkhabardhar_APP";


    public Sharedpref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void resetSharedPref() {
        editor.clear().commit();
    }

    public void apply() {
        editor.apply();
    }

    public void commit() {
        editor.commit();
    }
    public void setSaveName(String saveName){
        editor.putString("savename",saveName);
    }
    public String getSaveName(){
        return sharedPreferences.getString("savename","");
    }
    public void setPhoneNumbersListSize(int PhoneNumbersList){
        editor.putInt("PhoneNumbersListSize",PhoneNumbersList);
    }
    public int getPhoneNumbersLisSize(){
        return sharedPreferences.getInt("PhoneNumbersListSize",0);
    }
    public void setPrevPhoneNumbersListSize(int PhoneNumbersList){
        editor.putInt("PrevPhoneNumbersListSize",PhoneNumbersList);
    }
    public int getPrevPhoneNumbersLisSize(){
        return sharedPreferences.getInt("PrevPhoneNumbersListSize",0);
    }
    public void setPhoneNumbers(String PhoneNumbersList){
        editor.putString("PhoneNumbersList",PhoneNumbersList);
    }
    public String getPhoneNumbers(){
        return sharedPreferences.getString("PhoneNumbersList","");
    }
    public void setOutgoingNumbers(String outgoingNumbers){
        editor.putString("outgoingnumbers",outgoingNumbers);
    }
    public String getOutgoingNumbers(){
        return sharedPreferences.getString("outgoingnumbers","");
    }
    public void setRecievedNumbers(String recievednumbers){
        editor.putString("recievednumbers",recievednumbers);
    }
    public String getRecievedNumbers(){
        return sharedPreferences.getString("recievednumbers","");
    }
    public void setMissedCallNumber(String missedCallNumber){
        editor.putString("missedCallNumber",missedCallNumber);
    }
    public String getMissedCallNumber(){
        return sharedPreferences.getString("missedCallNumber","");
    }

    public void setSaveTextWritten(String saveTextWritten){
        editor.putString("saveTextWritten",saveTextWritten);
    }
    public String getSaveTextWritten(){
        return sharedPreferences.getString("saveTextWritten","");
    }

    public void setPhoneAppdetails(String phoneAppdetails){
        editor.putString("phoneAppdetails",phoneAppdetails);
    }
    public String getPhoneAppdetails(){
        return sharedPreferences.getString("phoneAppdetails","");
    }

    public void setPhoneAppdetailsListSize(int phoneAppdetailslistsize){
        editor.putInt("phoneAppdetailslistsize",phoneAppdetailslistsize);
    }
    public int getPhoneAppdetailsListSize(){
        return sharedPreferences.getInt("phoneAppdetailslistsize",0);
    }

    public void setPrevPhoneAppdetailsListSize(int phoneAppdetailslistsize){
        editor.putInt("prevphoneAppdetailslistsize",phoneAppdetailslistsize);
    }
    public int getPrevPhoneAppdetailsListSize(){
        return sharedPreferences.getInt("prevphoneAppdetailslistsize",0);
    }
    public void setNotificationData(String saveNotificationData){
        editor.putString("saveNotificationData",saveNotificationData);
    }
    public String getNotificationData(){
        return sharedPreferences.getString("saveNotificationData","");
    }
    public void setSmsData(String SmsData){
        editor.putString("SmsData",SmsData);
    }
    public String getSmsData(){
        return sharedPreferences.getString("SmsData","");
    }
}
