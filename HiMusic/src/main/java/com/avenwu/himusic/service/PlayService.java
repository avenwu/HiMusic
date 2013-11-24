package com.avenwu.himusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.avenwu.himusic.utils.Logger;

/**
 * @author chaobin
 * @date 11/22/13.
 */
public class PlayService extends Service {
    private final String TAG = PlayService.class.getSimpleName();
    private PlayBinder mBinder = new PlayBinder();
    private int mPlayingState = PlayState.IDLE;

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    public interface PlayState {
        int IDLE = 0;
        int PLAYING = 1;
        int PAUSED = 2;
        int STOPED = 3;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void toogle(){

    }
    public void play() {
        if (mPlayingState != PlayState.PLAYING) {
            Logger.d(TAG, "start play");
            mPlayingState = PlayState.PLAYING;
        }
    }

    public void pause() {
        Logger.d(TAG, "start pause");
        mPlayingState = PlayState.PAUSED;
    }

    public void resume() {
        Logger.d(TAG, "start resume");
        mPlayingState = PlayState.PLAYING;
    }

    public void stop() {
        Logger.d(TAG, "start stop");
        mPlayingState = PlayState.STOPED;
    }
}
