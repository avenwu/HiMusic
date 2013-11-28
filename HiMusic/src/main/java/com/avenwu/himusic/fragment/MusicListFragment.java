package com.avenwu.himusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avenwu.himusic.R;
import com.avenwu.himusic.manager.ReceiverHelper;
import com.avenwu.himusic.manager.SQLProvider;
import com.avenwu.himusic.manager.UIHelper;
import com.avenwu.himusic.manager.UriProvider;
import com.avenwu.himusic.modle.SongDetail;
import com.avenwu.himusic.task.ThumbnailFetchTask;
import com.avenwu.himusic.manager.CursorHelper;
import com.avenwu.himusic.manager.Logger;

/**
 * @author chaobin
 * @date 11/17/13.
 */
public class MusicListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = MusicListFragment.class.getSimpleName();
    private MusicAdapter mAdapter;
    private final int LOAD_SONGS = 0;
    private PlayReceiver mPlayReceiver;
    private int mCurrentPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MusicAdapter(getActivity());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOAD_SONGS, null, this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPosition = position;
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                playItem(cursor);
            }
        });
    }

    private void playItem(Cursor cursor) {
        SongDetail item = CursorHelper.getSongDetail(cursor);
        Logger.d(TAG, item.toString());
        ReceiverHelper.notifyPlay(getActivity(), item);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverHelper.INTENT_PLAY_PRE);
        filter.addAction(ReceiverHelper.INTENT_PLAY_NEXT);
        filter.addAction(ReceiverHelper.INTENT_UPDATE_POSITION);
        mPlayReceiver = new PlayReceiver();
        getActivity().registerReceiver(mPlayReceiver, filter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        switch (id) {
            case LOAD_SONGS:
                String[] projection = SQLProvider.getMusicProjection();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                String selection = MediaStore.Audio.Media.IS_MUSIC + ">0";
                loader = new CursorLoader(getActivity(), uri, projection, selection, null, sortOrder);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor == null) return;
        switch (cursorLoader.getId()) {
            case LOAD_SONGS:
                mAdapter.changeCursor(cursor);
                int position = cursor.getPosition();
                long lastID = UIHelper.getCurrentId(getActivity());
                Logger.d("TEST_CURSOR", position + "");
                cursor.moveToFirst();
                while (!cursor.isLast()) {
                    if (lastID == CursorHelper.getLong(cursor, MediaStore.MediaColumns._ID)) {
                        mCurrentPosition = cursor.getPosition();
                        Logger.d("TEST_CURSOR", "find position:" + mCurrentPosition);
                        break;
                    }
                    cursor.moveToNext();
                }
                cursor.moveToPosition(position);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (mAdapter != null) mAdapter.changeCursor(null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayReceiver != null) {
            getActivity().unregisterReceiver(mPlayReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class MusicAdapter extends SimpleCursorAdapter {
        private LayoutInflater mInflator;
        private Bitmap mDefaultBitmap;

        public MusicAdapter(Context context) {
            super(context, R.layout.list_song_item, null, new String[]{}, new int[]{}, 0);
            mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_widget_album);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflator.inflate(R.layout.list_song_item, null);
            ViewHolder holder = new ViewHolder();
            holder.photoView = (ImageView) view.findViewById(R.id.iv_photo);
            holder.title = (TextView) view.findViewById(R.id.tv_title);
            holder.artist = (TextView) view.findViewById(R.id.tv_artist);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            Uri uri = UriProvider.getArtistAlbumUri(cursor);
            new ThumbnailFetchTask(holder.photoView).execute(uri);
            SongDetail item = CursorHelper.getSongDetail(cursor);
            holder.artist.setText(item.artist);
            holder.title.setText(item.title);
            holder.artist.setSelected(true);
            holder.title.setSelected(true);
            holder.photoView.setImageBitmap(mDefaultBitmap);
            Logger.d(MusicAdapter.class.getSimpleName(), "bindView," + item.title);
        }

        private static class ViewHolder {
            ImageView photoView;
            TextView title;
            TextView artist;
        }
    }

    private class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ReceiverHelper.INTENT_PLAY_PRE.equals(action)) {
                if (mCurrentPosition > 0) {
                    mCurrentPosition--;
                    Cursor cursor = (Cursor) mAdapter.getItem(mCurrentPosition);
                    playItem(cursor);
                }
            } else if (ReceiverHelper.INTENT_PLAY_NEXT.equals(action)) {
                if (mCurrentPosition + 1 < mAdapter.getCount() - 1) {
                    mCurrentPosition++;
                    Cursor cursor = (Cursor) mAdapter.getItem(mCurrentPosition);
                    playItem(cursor);
                }
            } else if (ReceiverHelper.INTENT_UPDATE_POSITION.equals(action)) {
                mCurrentPosition = intent.getIntExtra("position", 0);
            }
        }
    }
}
