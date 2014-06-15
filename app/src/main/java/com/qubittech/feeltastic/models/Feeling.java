package com.qubittech.feeltastic.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Manoj on 13/05/2014.
 */
public class Feeling implements Serializable {

    private String feelingText;
    private String feelingTextLower;
    private String feelingDate;
    private String reason;
    private String action;
    private String userName;
    private double latitude;
    private double longitude;
    private boolean isFirstFeeling;


    private List<Comment> comments;

    public String getFeelingText() {
        return this.feelingText;
    }

    public void setFeelingText(String feelingtext) {
        this.feelingText = feelingtext;
    }

    public String getFeelingTextLower() {
        return this.feelingTextLower;
    }

    public void setFeelingTextLower(String feelingtextlower) {
        this.feelingTextLower = feelingtextlower;
    }

    public String getFeelingDate() {
        return this.feelingDate;
    }

    public void setFeelingDate(String feelingDate) {
        this.feelingDate = feelingDate;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isFirstFeeling() {
        return isFirstFeeling;
    }

    public void setFirstFeeling(boolean isFirstFeeling) {
        this.isFirstFeeling = isFirstFeeling;
    }

    public String getFeelingFormattedText(String pronoun) {
        String p = pronoun.equals("I") ? "am" : "is";

        return String.format("%s feeling %s because %s", isFirstFeeling ? p : "was", feelingText, reason);
    }
}
