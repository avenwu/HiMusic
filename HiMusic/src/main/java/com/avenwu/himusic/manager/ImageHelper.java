package com.avenwu.himusic.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chaobin
 * @date 11/18/13.
 */
public class ImageHelper {
    public static Bitmap resolveUri(String path, Context context) {
        if (path == null) return null;
        Bitmap bitmap = ThumbnailCache.getInstance(context).get(path);
        if (bitmap == null) {
            Uri uri = Uri.parse(path);
            String scheme = uri.getScheme();
            if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                    || ContentResolver.SCHEME_FILE.equals(scheme)) {
                InputStream stream = null;
                try {
                    stream = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(stream);
                    Logger.d(ImageHelper.class.getSimpleName(), "decode bitmap from " + path);
                } catch (Exception e) {
                    Logger.w(ImageHelper.class.getSimpleName(), "Unable to open content: " + uri, e);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log.w(ImageHelper.class.getSimpleName(), "Unable to close content: " + uri, e);
                        }
                    }
                }
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }
            ThumbnailCache.getInstance(context).add(path, bitmap);
        } else {
            Logger.d(ImageHelper.class.getSimpleName(), "hit cache bitmap, " + path);
        }
        if (bitmap == null) {
            Logger.w(ImageHelper.class.getSimpleName(), "resolveUri failed on bad bitmap uri: " + path);
        }
        return bitmap;
    }

}
