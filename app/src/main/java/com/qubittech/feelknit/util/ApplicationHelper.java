package com.qubittech.feelknit.util;

import android.app.Application;

import java.util.List;

public class ApplicationHelper extends Application {

    private String userName;
    private List<String> feelTexts;
    private String avatar;
    private String authorizationToken;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFeelTexts() {
        return feelTexts;
    }

    public void setFeelTexts(List<String> feelTexts) {
        this.feelTexts = feelTexts;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

//public static String UserName;
}
