package com.avenwu.himusic.manager;

import java.text.DecimalFormat;

/**
 * @author chaobin
 * @date 11/22/13.
 */
public class FormatterHelper {
    public static final float UNIT_K = 1024;
    public static final int UNIT_T = 1000;

    public static String getReadableSize(long size) {
        String result;

        if (size < UNIT_K) {
            result = size + "B";
        } else if ((size /= UNIT_K) < UNIT_K) {
            result = new DecimalFormat("#.00").format(size) + "KB";
        } else {
            result = new DecimalFormat("#.00").format(size / UNIT_K) + "M";
        }
        return result;
    }

    public static String getReadleDuration(long duration) {
//        if(duration<1000)
//        int seconds = (int) (duration / 1000) % 60 ;
//        int minutes = (int) ((duration / (1000*60)) % 60);
//        int hours   = (int) ((duration / (1000*60*60)) % 24);
        return duration+"";
    }
}
