package com.avenwu.himusic.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author chaobin
 * @date 11/15/13.
 */
public class UIHelper {
    public static void toast(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, CharSequence content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
