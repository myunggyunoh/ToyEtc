package com.example.toyetc.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String PREFERENCE_NAME = "preference_data";
    private static PreferenceUtil preferencemodule = null;
    private static Context mContext;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public static PreferenceUtil getInstance(Context context) {
        mContext = context;

        if (preferencemodule == null) {
            preferencemodule = new PreferenceUtil();
        }

        if(prefs==null){
            prefs = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }
        return preferencemodule;
    }


    private int save(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
        return value;
    }

    private String save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        return value;
    }

    private long save(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
        return value;
    }

    private boolean save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
        return value;
    }

    public String getCallNumber()                    { return prefs.getString("callNumber",""); }
    public String setCallNumber(String callNumber)   { return          save("callNumber", callNumber); }


    public String getMoneyStandard()                    { return prefs.getString("moneyStandard",""); }
    public String setmoneyStandard(String moneyStandard){ return                save("moneyStandard", moneyStandard); }


    public String getProductStandardStart()                            { return prefs.getString("productStandardStart",""); }
    public String setProductStandardStart(String productStandardStart) { return save("productStandardStart", productStandardStart); }


    public String getProductStandardEnd()                         { return prefs.getString("productStandardEnd",""); }
    public String setProductStandardEnd(String productStandardEnd){ return save("productStandardEnd", productStandardEnd); }


}