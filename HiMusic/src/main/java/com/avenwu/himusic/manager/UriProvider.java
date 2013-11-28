package com.avenwu.himusic.manager;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * @author chaobin
 * @date 11/18/13.
 */
public class UriProvider {
    public static Uri getArtistAlbumUri(Cursor cursor) {
        return MediaStore.Audio.Artists.Albums.getContentUri("external",
                CursorHelper.getLong(cursor, BaseColumns._ID));
    }
}
