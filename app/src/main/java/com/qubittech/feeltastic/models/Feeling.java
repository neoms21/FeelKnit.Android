package com.qubittech.feeltastic.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Manoj on 13/05/2014.
 */
public class Feeling implements Serializable {

    private String feelingText;
    private String feelingTextLower;
    private Date feelingDate;
    private String reason;
    private String action;


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

    public Date getFeelingDate() {
        return this.feelingDate;
    }

    public void setFeelingDate(Date feelingdate) {
        this.feelingDate = feelingdate;
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


}
