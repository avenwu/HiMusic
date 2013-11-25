package com.avenwu.himusic.utils;

import android.database.Cursor;
import android.provider.MediaStore;

import com.avenwu.himusic.modle.SongDetail;

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

    public static SongDetail getSongDetail(Cursor cursor) {
        SongDetail item = new SongDetail();
        item.id = CursorHelper.getLong(cursor, MediaStore.Audio.Media._ID);
        item.artist = CursorHelper.getString(cursor, MediaStore.Audio.Media.ARTIST);
        item.title = CursorHelper.getString(cursor, MediaStore.Audio.Media.TITLE);
        item.size = CursorHelper.getLong(cursor, MediaStore.Audio.Media.SIZE);
        item.duration = CursorHelper.getLong(cursor, MediaStore.Audio.Media.DURATION);
        return item;
    }
}
