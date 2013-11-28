package com.avenwu.himusic.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.avenwu.himusic.modle.SongDetail;

/**
 * Created by Aven on 13-11-24.
 */
public class ReceiverHelper {
    public static final String INTENT_UPDATE_PLAY_ITEM = "com.avenwu.himusic.update_status";
    public static final String INTENT_PLAY_MUSIC = "com.avenwu.himusic.play_song";
    public static final String INTENT_PLAY_PRE = "com.avenwu.himusic.play_pre";
    public static final String INTENT_PLAY_NEXT = "com.avenwu.himusic.play_next";
    public static final String INTENT_UPDATE_POSITION = "com.avenwu.himusic.update_position";

    public static void notifyPlay(Context context, SongDetail data) {
        Bundle bundle = new Bundle();
        bundle.putString("from", "MusicListFragment");
        bundle.putLong("id", data.id);
        bundle.putString("title", data.title);
        bundle.putString("artist", data.artist);
        context.sendBroadcast(new Intent(INTENT_PLAY_MUSIC).putExtras(bundle));
    }

    public static void notifyStatus(Context context, SongDetail data) {
        Bundle bundle = new Bundle();
        bundle.putString("from", "MusicListFragment");
        bundle.putLong("id", data.id);
        bundle.putString("title", data.title);
        bundle.putString("artist", data.artist);
        context.sendBroadcast(new Intent(INTENT_UPDATE_PLAY_ITEM).putExtras(bundle));
    }

    public static void notifyPre(Context context) {
        context.sendBroadcast(new Intent(INTENT_PLAY_PRE));
    }

    public static void notifyNext(Context context) {
        context.sendBroadcast(new Intent(INTENT_PLAY_NEXT));
    }

    public static void notifyPosition(Context context, int position) {
        context.sendBroadcast(new Intent(INTENT_PLAY_NEXT).putExtra("position", position));
    }
}
