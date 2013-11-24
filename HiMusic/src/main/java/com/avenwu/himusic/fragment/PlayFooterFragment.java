package com.avenwu.himusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

/**
 * Created by Aven on 13-11-24.
 */
public class PlayFooterFragment extends Fragment {
    private ImageView mArtistPhoto;
    private Button mPlayPause;
    private Button mNext;
    private TextView mArtistName;
    private TextView mSongTile;
    private PlayReceiver mPlayReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_footer_layout, null);
        mArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
        mPlayPause = (Button) view.findViewById(R.id.btn_play_pause);
        mNext = (Button) view.findViewById(R.id.btn_next);
        mArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
        mSongTile = (TextView) view.findViewById(R.id.tv_song_title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlayReceiver = new PlayReceiver();
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
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toast(getActivity(), "next clicked");
            }
        });
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
        }
    }
}
