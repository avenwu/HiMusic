package com.avenwu.himusic.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.avenwu.himusic.utils.Logger;

/**
 * @author chaobin
 * @date 11/22/13.
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class PlayService extends Service {
    private final String TAG = PlayService.class.getSimpleName();
    private PlayBinder mBinder = new PlayBinder();
    private int mPlayingState = PlayState.IDLE;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private Uri mMusicUri;
    private boolean AUDIO_FOCUS_ABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    private AudioManager.OnAudioFocusChangeListener mFocusChangedListener = AUDIO_FOCUS_ABLE ?
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        pause();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        stop();
                        mAudioManager.abandonAudioFocus(mFocusChangedListener);
                    }
                }
            } : null;

    public class PlayBinder extends Binder {
        public PlayService getService() {
            mAudioManager = AUDIO_FOCUS_ABLE ?
                    (AudioManager) getSystemService(Context.AUDIO_SERVICE) : null;
            return PlayService.this;
        }
    }

    public interface PlayState {
        int IDLE = 0;
        int PLAYING = 1;
        int PAUSED = 2;
        int STOPPED = 3;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void play() {
        Logger.d(TAG, "start play");
        if (AUDIO_FOCUS_ABLE) {
            int result = mAudioManager.requestAudioFocus(mFocusChangedListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                start();
            }
        } else {
            start();
        }
        mPlayingState = PlayState.PLAYING;
    }

    private void start() {
        try {
            if (mMediaPlayer == null) {
                initMediaPlayer();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.prepareAsync();
            } else if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMediaPlayer() throws Exception {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(this, mMusicUri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void pause() {
        Logger.d(TAG, "start pause");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mPlayingState = PlayState.PAUSED;
    }

    public void stop() {
        Logger.d(TAG, "start stop");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mPlayingState = PlayState.STOPPED;
    }

    public void pre() {

    }

    public void next() {

    }

    public void updateUri(Uri uri) {
        mMusicUri = uri;
    }
}
