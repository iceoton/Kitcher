package com.itonlab.kitcher.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.itonlab.kitcher.R;

public class AppPreference {
    private Context mContext;
    SharedPreferences sharedPref;

    public AppPreference(Context mContext) {
        this.mContext = mContext;
        sharedPref = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void saveMasterIP(String ip){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("masterIP", ip);
        editor.apply();
    }

    public String getMasterIP(){
        return sharedPref.getString("masterIP", mContext.getResources().getString(R.string.default_ip_address));
    }

    public void saveYourName(String name){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("yourName",name);
        editor.apply();
    }

    public String getYourName(){
        return sharedPref.getString("yourName",mContext.getResources().getString(R.string.app_name));
    }
}
