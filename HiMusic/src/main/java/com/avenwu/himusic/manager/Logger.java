package com.avenwu.himusic.manager;

import android.util.Log;

import com.avenwu.himusic.BuildConfig;

/**
 * @author chaobin
 * @date 11/18/13.
 */
public class Logger {
    public static void w(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, content);
        }
    }

    public static void d(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void i(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }
}
