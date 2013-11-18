package com.avenwu.himusic.utils;

import android.database.Cursor;

/**
 * @author chaobin
 * @date 11/18/13.
 */
public class CursorHelper {

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));

    }


}
