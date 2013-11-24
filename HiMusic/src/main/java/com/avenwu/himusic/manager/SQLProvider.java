package com.avenwu.himusic.manager;

import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * @author chaobin
 * @date 11/22/13.
 */
public class SQLProvider {
    public static String[] getMusicProjection() {
        return new String[]{
                BaseColumns._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION
        };
    }
}
