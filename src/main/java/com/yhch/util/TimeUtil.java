package com.yhch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zlren on 2017/7/2.
 */
public class TimeUtil {

    /**
     * 解析时间
     *
     * @param timeString
     * @return
     */
    public static Date parseTime(String timeString) {

        if (!Validator.checkEmpty(timeString)) {
            try {
                timeString = timeString.replace("Z", " UTC");
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z").parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    /**
     * 得到当前时间
     *
     * @return
     * @throws ParseException
     */
    public static Date getCurrentTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        return simpleDateFormat.parse(format);
    }


    /**
     * 返回一年后的时间
     *
     * @return
     */
    public static Date getOneYearAfterTime() {
        return getTimeAfterMonths(12);
    }

    /**
     * 返回几个月后的时间
     *
     * @param n
     * @return
     */
    public static Date getTimeAfterMonths(Integer n) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, n);
        date = calendar.getTime();
        return date;
    }
}
