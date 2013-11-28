package com.avenwu.himusic.fragment;


import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avenwu.himusic.R;
import com.avenwu.himusic.manager.Logger;
import com.avenwu.himusic.manager.ReceiverHelper;
import com.avenwu.himusic.manager.UIHelper;
import com.avenwu.himusic.service.PlayService;
import com.avenwu.himusic.widget.PlayPauseButton;

/**
 * Created by Aven on 13-11-24.
 */
public class PlayFooterFragment extends Fragment {
    private ImageView mArtistPhoto;
    private PlayPauseButton mPlayPause;
    private View mNext;
    private View mPre;
    private TextView mArtistName;
    private TextView mSongTile;
    private PlayReceiver mPlayReceiver;
    private PlayService mPlayService;
    private long mCurrentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentId = UIHelper.getCurrentId(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_footer_layout, null);
        mArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
        mPlayPause = (PlayPauseButton) view.findViewById(R.id.btn_play_pause);
        mNext = view.findViewById(R.id.btn_next);
        mPre = view.findViewById(R.id.btn_play_pre);
        mArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
        mSongTile = (TextView) view.findViewById(R.id.tv_song_title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mArtistName.setSelected(true);
        mSongTile.setSelected(true);
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverHelper.INTENT_UPDATE_PLAY_ITEM);
        filter.addAction(ReceiverHelper.INTENT_PLAY_MUSIC);
        mPlayReceiver = new PlayReceiver();
        getActivity().registerReceiver(mPlayReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        UIHelper.saveCurrentId(getActivity(), mCurrentId);
        if (mPlayReceiver != null) {
            getActivity().unregisterReceiver(mPlayReceiver);
        }
    }

    public class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(PlayFooterFragment.class.getSimpleName(), action);
            if (ReceiverHelper.INTENT_PLAY_MUSIC.equals(action)) {
                updateArtistInfo(intent);
                long id = intent.getLongExtra("id", 0);
                if (id != 0 && mPlayService != null) {
                    mCurrentId = id;
                    mPlayService.updateUri(ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id));
                    mPlayService.stop();
                    mPlayPause.play();
                    mPlayPause.setChecked(true);
                }
            } else if (ReceiverHelper.INTENT_UPDATE_PLAY_ITEM.equals(action)) {
                updateArtistInfo(intent);
            }
        }
    }

    private void updateArtistInfo(Intent intent) {
        mArtistName.setText(intent.getStringExtra("artist"));
        mSongTile.setText(intent.getStringExtra("title"));
    }

    public void bindService(PlayService service) {
        mPlayService = service;
        if (mCurrentId != -1)
            mPlayService.queryHistoryItem(mCurrentId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UIHelper.saveCurrentId(getActivity(), mCurrentId);
    }

    private void setListeners() {
        mArtistPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "click photo");
            }
        });
        mPlayPause.setOnPlayPauseListener(new PlayPauseButton.OnPlayPauseListener() {
            @Override
            public void onPlay() {
                mPlayService.play();
            }

            @Override
            public void onPause() {
                mPlayService.pause();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiverHelper.notifyNext(getActivity());
            }
        });
        mPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiverHelper.notifyPre(getActivity());
            }
        });
    }
}
