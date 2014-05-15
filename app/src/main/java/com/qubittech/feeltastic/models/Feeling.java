package com.qubittech.feeltastic.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Manoj on 13/05/2014.
 */
public class Feeling implements Serializable {

    private String FeelingText;
    private String FeelingTextLower;
    private Date FeelingDate;
    private String Reason;
    private String Action;


    public String getFeelingText() {return this.FeelingText;}
    public void setFeelingText(String feelingtext) { this.FeelingText = feelingtext;}

    public String getFeelingTextLower() {return this.FeelingTextLower;}
    public void setFeelingTextLower(String feelingtextlower) { this.FeelingTextLower = feelingtextlower;}

    public Date getFeelingDate() {return this.FeelingDate;}
    public void setFeelingDate(Date feelingdate) { this.FeelingDate = feelingdate;}

    public String getReason() {return this.Reason;}
    public void setReason(String reason) { this.Reason = reason;}

    public String getAction() {return this.Action;}
    public void setAction(String action) { this.Action = action;}


}
