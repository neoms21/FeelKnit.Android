package com.qubittech.feelknit.util;

import com.qubittech.feelknit.app.LoadingActivity;
import com.qubittech.feelknit.app.LoginActivity;
import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.RegistrationActivity;
import com.qubittech.feelknit.app.SaveAvatarActivity;

/**
 * Created by Manoj on 31/01/2015.
 */
public class App {

    public static MainActivity mainActivity;
    public static LoginActivity loginActivity;
    public static LoadingActivity loadingActivity;
    public static SaveAvatarActivity saveAvatarActivity;
    public static RegistrationActivity registrationActivity;

    public static void close() {
        if (App.mainActivity != null)
            mainActivity.finish();
        if (App.loginActivity != null)
            loginActivity.finish();
        if (App.saveAvatarActivity!= null)
            saveAvatarActivity.finish();
        if (App.registrationActivity != null)
            registrationActivity.finish();
        if (App.loadingActivity!= null)
            loadingActivity.finish();
    }
}
