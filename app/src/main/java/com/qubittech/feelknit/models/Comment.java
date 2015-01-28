package com.qubittech.feelknit.models;

import java.io.Serializable;

public class Comment implements Serializable {

    private String id;
    private String text;
    private String user;
    private String postedAt;
    private String userAvatar;
    private boolean isReported;

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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean isReported) {
        this.isReported = isReported;
    }
}
