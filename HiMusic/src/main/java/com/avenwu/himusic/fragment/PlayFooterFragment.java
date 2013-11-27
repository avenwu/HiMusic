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
import com.avenwu.himusic.manager.ReceiverHelper;
import com.avenwu.himusic.service.PlayService;
import com.avenwu.himusic.utils.UIHelper;
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
        mPlayReceiver = new PlayReceiver();

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
                mPlayService.next();
            }
        });
        mPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayService.pre();
            }
        });
    }

    private void reset() {
        mPlayService.stop();
        mPlayPause.play();
        mPlayPause.setChecked(true);
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
            if (id != 0 && mPlayService != null) {
                mPlayService.updateUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id));
                reset();
            }
        }
    }

    public void bindService(PlayService service) {
        mPlayService = service;
    }
}
