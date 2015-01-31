package com.qubittech.feelknit.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class ApplicationHelper {

    private static String USER_INFO = "UserInfo";

    public static String getUserName(Context context) {
        return getFromPreferences(context, "Username");
    }


    public static void setUserName(Context context, String userName) {
        saveInPreferences(context, "Username", userName);
    }


    public static String getFeelTexts(Context context) {
        return getFromPreferences(context, "Feelings");
    }

    public static void setFeelTexts(Context context, String feelTexts) {
        saveInPreferences(context, "Feelings", feelTexts);
    }

    public static String getAvatar(Context context) {
        return getFromPreferences(context, "Avatar");
    }

    public static void setAvatar(Context context, String avatar) {
        saveInPreferences(context, "Avatar", avatar);
    }

    public static String getAuthorizationToken(Context context) {
        return getFromPreferences(context, "Token");
    }

    public static void setAuthorizationToken(Context context, String authorizationToken) {
        saveInPreferences(context, "Token", authorizationToken);
    }

    public static String getUserEmail(Context context) {
        return getFromPreferences(context, "Email");
    }

    public static void setUserEmail(Context context, String userEmail) {
        saveInPreferences(context, "Email", userEmail);
    }

    private static void saveInPreferences(Context context, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    private static String getFromPreferences(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

//public static String UserName;
}
