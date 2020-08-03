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
    public void setOutgoingNumbers(String outgoingNumbers){
        editor.putString("outgoingnumbers",outgoingNumbers);
    }
    public String getOutgoingNumbers(){
        return sharedPreferences.getString("outgoingnumbers","");
    }
    public void setIncomingNumbers(String inComingNumbers){
        editor.putString("incomingnumbers",inComingNumbers);
    }
    public String getIncomingNumbers(){
        return sharedPreferences.getString("incomingnumbers","");
    }
}
