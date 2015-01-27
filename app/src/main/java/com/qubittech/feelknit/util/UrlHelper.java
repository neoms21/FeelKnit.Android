package com.qubittech.feelknit.util;

import android.os.Build;

public class UrlHelper {

    static boolean onEmulator;

    static {
        onEmulator = isRunningOnEmulator();
    }

    private static final String BASE_URL_EMULATOR = "http://10.0.3.2/FeelKnitService/";
    private static final String BASE_URL = "http://feelknitservice.apphb.com/";
    public static final String COMMENTS = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "Comments";
    public static final String FEELINGS = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "feelings";
    public static final String USER_LOGIN = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "Users/login";
    public static final String USER_KEY = (onEmulator ? BASE_URL_EMULATOR  : BASE_URL )+ "Users/clientkey";
    public static final String CLEAR_USER_KEY = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "Users/clearkey";
    public static final String USER = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "Users";
    public static final String SAVE_AVATAR= (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "Users/saveAvatar";
    public static final String USERNAME = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "feelings/username/";
    public static final String INCREASE_SUPPORT = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "feelings/increasesupport";
    public static final String DECREASE_SUPPORT = (onEmulator ? BASE_URL_EMULATOR : BASE_URL )+ "feelings/decreasesupport";
    public static final String COMMENTSFEELING = (onEmulator ? BASE_URL_EMULATOR  : BASE_URL )+ "feelings/comments/%s";
    public static final String EMAILREPORT = (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "email/report";
    public static final String REPORTCOMMENT= (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "email/report";
    public static final String USER_FEELINGS = (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "feelings/userfeelings";
    public static final String GET_FEELS = (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "feelings/getfeels";
    public static final String RELATED_FEELINGS= (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "feelings/relatedfeelings/%s";
    public static final String SAVE_USER= (onEmulator ? BASE_URL_EMULATOR : BASE_URL)  + "Users/saveuser";


    public static boolean isRunningOnEmulator() {

        onEmulator = Build.FINGERPRINT.startsWith("generic")//
                || Build.FINGERPRINT.startsWith("unknown")//
                || Build.MODEL.contains("google_sdk")//
                || Build.MODEL.contains("Emulator")//
                || Build.MODEL.contains("Android SDK built for x86");
        if (onEmulator)
            return true;
        onEmulator |= Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
        if (onEmulator)
            return true;
        onEmulator |= "google_sdk".equals(Build.PRODUCT);
        return onEmulator;
    }

}
