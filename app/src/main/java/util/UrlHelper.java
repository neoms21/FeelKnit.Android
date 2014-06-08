package util;

import android.os.Build;

/**
 * Created by Manoj on 01/06/2014.
 */
public class UrlHelper {
    static boolean onEmulator;

    static {
        onEmulator = isRunningOnEmulator();
    }

    private static final String BASE_URL_EMULATOR = "http://10.0.3.2/FeelKnitService/";
    private static final String BASE_URL = "http://feelknitservice.apphb.com/";
    public static final String FEELINGS = onEmulator ? BASE_URL_EMULATOR + "feelings" : BASE_URL + "feelings";
    public static final String USER_VERIFY = onEmulator ? BASE_URL_EMULATOR + "Users/Verify" : BASE_URL + "Users/Verify";
    public static final String USER = onEmulator ? BASE_URL_EMULATOR + "Users" : BASE_URL + "Users";
    public static final String USERNAME = onEmulator ? BASE_URL_EMULATOR + "feelings/username/" : BASE_URL + "feelings/username/";


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