package com.qubittech.feelknit.models;

/**
 * Created by Manoj on 15/11/2014.
 */
public class LoginResult {
    private boolean isLoginSuccessful;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isLoginSuccessful() {
        return isLoginSuccessful;
    }

    public void setLoginSuccessful(boolean isLoginSuccessful) {
        this.isLoginSuccessful = isLoginSuccessful;
    }
}
