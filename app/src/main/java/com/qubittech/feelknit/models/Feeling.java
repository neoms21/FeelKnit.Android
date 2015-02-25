package com.qubittech.feelknit.models;

import com.qubittech.feelknit.util.Utilities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Manoj on 13/05/2014.
 */
public class Feeling implements Serializable {

    private String id;
    private String feelingText;
    private String feelingTextLower;
    private String feelingDate;
    private String reason;
    private String action;
    private String userName;
    private double latitude;
    private double longitude;
    private boolean isFirstFeeling;
    private boolean isReported;
    private int supportCount;
    private List<Comment> comments;
    private List<String> supportUsers;
    private String userAvatar;

    public String getFeelingText() {
        return this.feelingText;
    }

    public void setFeelingText(String feelingText) {
        this.feelingText = feelingText;
    }

    public String getFeelingTextLower() {
        return this.feelingTextLower;
    }

    public void setFeelingTextLower(String feelingTextLower) {
        this.feelingTextLower = feelingTextLower;
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

        if (Utilities.isNullOrBlank(reason) && Utilities.isNullOrBlank(action))
            return String.format("%s feeling %s", isFirstFeeling ? p : "was", feelingText);
        if (Utilities.isNullOrBlank(reason) && !Utilities.isNullOrBlank(action))
            return String.format("%s feeling %s", isFirstFeeling ? p : "was", feelingText);
        if (!Utilities.isNullOrBlank(reason) && Utilities.isNullOrBlank(action))
            return String.format("%s feeling %s because %s", isFirstFeeling ? p : "was", feelingText, reason);

        return String.format("%s feeling %s because %s", isFirstFeeling ? p : "was", feelingText, reason);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    public List<String> getSupportUsers() {
        return supportUsers;
    }

    public void setSupportUsers(List<String> supportUsers) {
        this.supportUsers = supportUsers;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean isReported) {
        this.isReported = isReported;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}
