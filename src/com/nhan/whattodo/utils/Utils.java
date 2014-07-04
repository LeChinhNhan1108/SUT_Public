package com.nhan.whattodo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivanle on 7/3/14.
 */
public class Utils {

    /* Date Time Format */

    public static String convertDateToString(long value){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date  = new Date(value);
        return simpleDateFormat.format(date);
    }

}
