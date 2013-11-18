package com.avenwu.himusic.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.avenwu.himusic.utils.Logger;

/**
 * @author chaobin
 * @date 11/17/13.
 */
public class ThumbnailCache {
    private static final String TAG = ThumbnailCache.class.getSimpleName();

    private LruCache<String, Bitmap> mLruCache;

    private static volatile ThumbnailCache sInstance;

    private ThumbnailCache(final Context context) {
        init(context);
    }

    public static ThumbnailCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ThumbnailCache.class) {
                if (sInstance == null)
                    sInstance = new ThumbnailCache(context);
                Logger.d(TAG, "ThumbnailCache initialized");
            }
        }
        return sInstance;
    }

    public void init(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final int lruCacheSize = Math.round(0.25f * activityManager.getMemoryClass()
                * 1024 * 1024);
        mLruCache = new LruCache<String, Bitmap>(lruCacheSize) {
            @Override
            protected int sizeOf(final String paramString, final Bitmap paramBitmap) {
                return paramBitmap.getByteCount();
            }
        };
    }

    public void add(final String data, final Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }
        if (get(data) == null) {
            mLruCache.put(data, bitmap);
            Logger.d(TAG, "add bitmap cache, " + data);
        }
    }

    public final Bitmap get(final String data) {
        if (data == null) {
            return null;
        }
        if (mLruCache != null) {
            final Bitmap mBitmap = mLruCache.get(data);
            if (mBitmap != null) {
                Logger.d(TAG, "get bitmap cache, " + data);
                return mBitmap;
            }
        }
        return null;
    }

    public void remove(final String key) {
        if (mLruCache != null) {
            mLruCache.remove(key);
            Logger.d(TAG, "remove bitmap cache" + key);
        }
    }

    public void clearMemCache() {
        if (mLruCache != null) {
            mLruCache.evictAll();
            Logger.d(TAG, "clear cache map");
        }
        System.gc();
    }
}
