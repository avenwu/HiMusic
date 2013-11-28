package com.avenwu.himusic.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;

import com.avenwu.himusic.manager.CursorHelper;
import com.avenwu.himusic.manager.Logger;
import com.avenwu.himusic.manager.ReceiverHelper;
import com.avenwu.himusic.modle.SongDetail;

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
    private PlayReceiver mPlayReceiver;
    private Uri mMusicUri;

    public interface PlayState {
        int IDLE = 0;
        int PLAYING = 1;
        int PAUSED = 2;
        int STOPPED = 3;
    }

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

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverHelper.INTENT_UPDATE_PLAY_ITEM);
        mPlayReceiver = new PlayReceiver();
        registerReceiver(mPlayReceiver, filter);
        Logger.d(PlayService.class.getSimpleName(), "register play service");
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            mAudioManager = AUDIO_FOCUS_ABLE ?
                    (AudioManager) getSystemService(Context.AUDIO_SERVICE) : null;
            return PlayService.this;
        }
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

    public void queryHistoryItem(long id) {
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            SongDetail item = CursorHelper.getSongDetail(cursor);
            ReceiverHelper.notifyStatus(this, item);
//            ReceiverHelper.notifyPosition(this, cursor.getPosition());
            cursor.close();
        }
    }

    public void updateUri(Uri uri) {
        mMusicUri = uri;
    }

    public class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(PlayService.class.getSimpleName(), action);
            if (ReceiverHelper.INTENT_UPDATE_PLAY_ITEM.equals(action)) {
                long id = intent.getLongExtra("id", -1);
                if (id != -1) {
                    updateUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id));
                }
            } else if (ReceiverHelper.INTENT_PLAY_MUSIC.equals(action)) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayReceiver != null) unregisterReceiver(mPlayReceiver);
    }

}
