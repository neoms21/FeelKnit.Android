package util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.qubittech.feeltastic.services.TrackingService;

/**
 * Created by Manoj on 01/06/2014.
 */
public class UrlHelper {

    private static final String BASE_URL_EMULATOR = "http://10.0.3.2/FeelKnitService/";
    private static final String BASE_URL = "http://feelknitservice.apphb.com/";
    public static final String FEELINGS = isRunningOnEmulator() ? BASE_URL_EMULATOR + "feelings" : BASE_URL + "feelings";
    public static final String USER_VERIFY = isRunningOnEmulator() ? BASE_URL_EMULATOR + "Users/Verify" : BASE_URL + "Users/Verify";
    public static final String USER = isRunningOnEmulator() ? BASE_URL_EMULATOR + "Users" : BASE_URL + "Users";
    public static final String USERNAME = isRunningOnEmulator() ? BASE_URL_EMULATOR + "feelings/username/" : BASE_URL + "feelings/username/";


   public static boolean isRunningOnEmulator()
    {
        boolean result=//
                Build.FINGERPRINT.startsWith("generic")//
                        ||Build.FINGERPRINT.startsWith("unknown")//
                        ||Build.MODEL.contains("google_sdk")//
                        ||Build.MODEL.contains("Emulator")//
                        ||Build.MODEL.contains("Android SDK built for x86");
        if(result)
            return true;
        result|=Build.BRAND.startsWith("generic")&&Build.DEVICE.startsWith("generic");
        if(result)
            return true;
        result|="google_sdk".equals(Build.PRODUCT);
        return result;
    }

}
