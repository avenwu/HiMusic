package com.avenwu.himusic.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avenwu.himusic.R;
import com.avenwu.himusic.manager.UriProvider;
import com.avenwu.himusic.task.ThumbnailFetchTask;
import com.avenwu.himusic.manager.CursorHelper;
import com.avenwu.himusic.manager.Logger;

/**
 * @author chaobin
 * @date 11/17/13.
 */
public class ArtistAlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArtistAdapter mAdapter;
    private GridView mArtistGridView;
    private final int FETCH_ARTISTS = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_grid_layout, null);
        mArtistGridView = (GridView) view.findViewById(R.id.gv_songs);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArtistAdapter(getActivity());
        mArtistGridView.setAdapter(mAdapter);
        getLoaderManager().initLoader(FETCH_ARTISTS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        switch (id) {
            case FETCH_ARTISTS:
                String[] projection = {
                        BaseColumns._ID,
                        MediaStore.Audio.ArtistColumns.ARTIST,
                        MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS
                };
                Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
                String sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
                loader = new CursorLoader(getActivity(), uri, projection, null, null, sortOrder);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor == null) return;
        switch (cursorLoader.getId()) {
            case FETCH_ARTISTS:
                mAdapter.changeCursor(cursor);
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
    public void onDestroy() {
        super.onDestroy();
    }

    private static class ArtistAdapter extends SimpleCursorAdapter {
        private LayoutInflater mInflator;
        private Bitmap mDefaultBitmap;
        private final ViewGroup.LayoutParams mLayoutParams;

        public ArtistAdapter(Context context) {
            super(context, R.layout.grid_song_item, null, new String[]{}, new int[]{}, 0);
            mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_widget_album);
            final int mColumnWidth = context.getResources().getDisplayMetrics().widthPixels / 2;
            mLayoutParams = new ViewGroup.LayoutParams(mColumnWidth, mColumnWidth);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflator.inflate(R.layout.grid_song_item, null);
            view.setLayoutParams(mLayoutParams);
            ViewHolder holder = new ViewHolder();
            holder.photoView = (ImageView) view.findViewById(R.id.iv_photo);
            holder.title = (TextView) view.findViewById(R.id.tv_title);
            holder.albums = (TextView) view.findViewById(R.id.tv_albums_count);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            Uri uri = UriProvider.getArtistAlbumUri(cursor);
            new ThumbnailFetchTask(holder.photoView).execute(uri);
            holder.title.setText(CursorHelper.getString(cursor, MediaStore.Audio.ArtistColumns.ARTIST));
            final int count = CursorHelper.getInt(cursor, MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS);
            holder.albums.setText(context.getResources().getQuantityString(R.plurals.nalbums, count, count));
            holder.photoView.setImageBitmap(mDefaultBitmap);
            Logger.d(ArtistAdapter.class.getSimpleName(), "bindView," + holder.title.getText());
        }

        private static class ViewHolder {
            ImageView photoView;
            TextView title;
            TextView albums;
        }
    }
}
