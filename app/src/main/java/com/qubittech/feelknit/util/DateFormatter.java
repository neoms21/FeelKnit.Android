package com.qubittech.feelknit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
        String result = "";
        SimpleDateFormat intermediateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            result = intermediateFormat.format(sdf.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}

