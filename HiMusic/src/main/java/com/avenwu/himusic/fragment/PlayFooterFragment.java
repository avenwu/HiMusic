package com.avenwu.himusic.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avenwu.himusic.R;
import com.avenwu.himusic.manager.ReceiverHelper;
import com.avenwu.himusic.utils.UIHelper;

import java.io.IOException;

/**
 * Created by Aven on 13-11-24.
 */
public class PlayFooterFragment extends Fragment {
    private ImageView mArtistPhoto;
    private View mPlayPause;
    private View mNext;
    private View mPre;
    private TextView mArtistName;
    private TextView mSongTile;
    private PlayReceiver mPlayReceiver;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private boolean AUDIO_FOCUS_ABLE = Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO;
    private Uri mMusicUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_footer_layout, null);
        mArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
        mPlayPause = view.findViewById(R.id.btn_play_pause);
        mPlayPause.setSelected(false);
        mNext = view.findViewById(R.id.btn_next);
        mPre = view.findViewById(R.id.btn_play_pre);
        mArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
        mSongTile = (TextView) view.findViewById(R.id.tv_song_title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlayReceiver = new PlayReceiver();
        mAudioManager = AUDIO_FOCUS_ABLE ?
                (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE) : null;
        mArtistPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "click photo");
            }
        });
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "play button clicked");
                if (AUDIO_FOCUS_ABLE) {
                    mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int focusChange) {
                            switch (focusChange) {
                                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                    pausePlayback();
                                    break;
                                case AudioManager.AUDIOFOCUS_GAIN:
                                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                        pausePlayback();
                                    } else {
                                        play();
                                        mPlayPause.setPressed(true);
                                    }
                                    break;
                                case AudioManager.AUDIOFOCUS_LOSS:
                                    stopPlayback();
                                    break;
                            }
                        }
                    }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                } else {
                    play();
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "next clicked");
            }
        });
        mPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "pre clicked");
            }
        });
    }

    private void pausePlayback() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    private void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void reset() {
        stopPlayback();
        play();
    }

    private void play() {
        try {
            if (mMediaPlayer == null) initMediaPlayer();
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMediaPlayer() throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(getActivity(), mMusicUri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ReceiverHelper.PLAY_RECEIVER_INTENT_FILTER);
        getActivity().registerReceiver(mPlayReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mPlayReceiver);
    }

    /**
     * Created by Aven on 13-11-24.
     */
    public class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("from");
            UIHelper.toast(context, "play intent received," + from);
            mArtistName.setText(intent.getStringExtra("artist"));
            mSongTile.setText(intent.getStringExtra("title"));
            long id = intent.getLongExtra("id", 0);
            if (id != 0) {
                mMusicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                reset();
            }
        }
    }
}
