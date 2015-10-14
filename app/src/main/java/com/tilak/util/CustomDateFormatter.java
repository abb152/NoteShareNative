package com.tilak.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Jay on 14-10-2015.
 */
public class CustomDateFormatter {

    public String dbToAdapterDate(String date){

        SimpleDateFormat originalDateFormat  = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss");
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        try {
            date = requiredDateFormat.format(originalDateFormat.parse(date)).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
