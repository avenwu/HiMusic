package com.avenwu.himusic.task;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.avenwu.himusic.utils.CursorHelper;
import com.avenwu.himusic.utils.ImageHelper;
import com.avenwu.himusic.utils.Logger;

public class ThumbnailFetchTask extends AsyncTask<Uri, Void, Bitmap> {
    private ImageView mImageView;

    public ThumbnailFetchTask(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        Cursor cursor = mImageView.getContext().getContentResolver().query(params[0],
                new String[]{
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.ALBUM_ART
                },
                null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
        String path = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            Logger.d(ThumbnailFetchTask.class.getSimpleName(),
                    "artist: " + CursorHelper.getString(cursor, MediaStore.Audio.Artists.ARTIST) +
                            " album_art: " + path);
        }
        cursor.close();
        return ImageHelper.resolveUri(path, mImageView.getContext());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}