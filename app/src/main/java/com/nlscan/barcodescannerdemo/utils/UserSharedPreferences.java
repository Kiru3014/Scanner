package com.nlscan.barcodescannerdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by K.Gowda on 4/6/2017.
 */

public class UserSharedPreferences
{
    private static final String PREF_NAME = "UserPreferences";
    private static final String USERID = "userId";
    private static final String USERTYPE = "usertype";
    private static final String USERNAME = "username";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;



    // Constructor
    @SuppressLint("CommitPrefEdits")
    public UserSharedPreferences(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        editor = pref.edit();
    }


    public String getFirstName() {
        return pref.getString(USERNAME, "");
    }

    public void setFirstName(String firstName) {
        editor.putString(USERNAME, firstName);
        editor.commit();
    }



    public String getUserid() {
        return pref.getString(USERID, "");
    }

    public void setUserid(String userid) {
        editor.putString(USERID, userid);
        editor.commit();
    }


    public String getUserToker() {
        return pref.getString(USERTYPE, "");
    }

    public void setUserToken(String userToken) {
        editor.putString(USERTYPE, userToken);
        editor.commit();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

}

