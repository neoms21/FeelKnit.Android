package com.qubittech.feeltastic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sethi
 * Date: 24/03/13
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public class DateFormatter {

    public static String Format(String strDate)
    {
        strDate= strDate.replace('Z',' ');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yy HH:mm");
        Date date = new Date();
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf2.format(date);
    }
}

