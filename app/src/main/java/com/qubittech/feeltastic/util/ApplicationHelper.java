package com.qubittech.feeltastic.util;

import android.app.Application;

import java.util.List;

/**
 * Created by Manoj on 01/06/2014.
 */
public class ApplicationHelper extends Application {

    private String userName;
    private List<String> feelTexts;


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

//public static String UserName;
}
