package com.qubittech.feelknit.models;

import java.io.Serializable;

public class User implements Serializable {

    private String userName;
    private String avatar;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
