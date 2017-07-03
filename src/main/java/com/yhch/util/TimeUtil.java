package com.yhch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zlren on 2017/7/2.
 */
public class TimeUtil {

    public static Date parseTime(String timeString) {

        if (!Validator.checkEmpty(timeString)) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }
}
