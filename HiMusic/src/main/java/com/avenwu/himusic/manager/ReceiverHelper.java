package com.avenwu.himusic.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.avenwu.himusic.modle.SongDetail;

/**
 * Created by Aven on 13-11-24.
 */
public class ReceiverHelper {
    public static final String PLAY_RECEIVER_INTENT_FILTER = "com.avenwu.himusic.play_filter";

    public static void notifyPlay(Context context, SongDetail data) {
        Bundle bundle = new Bundle();
        bundle.putString("from", "MusicListFragment");
        bundle.putLong("id", data.id);
        bundle.putString("title", data.title);
        bundle.putString("artist", data.artist);
        context.sendBroadcast(new Intent(PLAY_RECEIVER_INTENT_FILTER).putExtras(bundle));
    }
}
