package com.qubittech.feeltastic.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Manoj on 28/05/2014.
 */
public class Comment implements Serializable {

    private String text;
    private String user;
    private String postedAt;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }
}
