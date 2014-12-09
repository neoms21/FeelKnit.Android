package com.qubittech.feelknit.models;

public class LoginResult {
    private boolean isLoginSuccessful;
    private String avatar;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
