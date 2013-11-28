package com.avenwu.himusic.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.avenwu.himusic.R;
import com.avenwu.himusic.fragment.ArtistAlbumsFragment;
import com.avenwu.himusic.fragment.MusicListFragment;
import com.avenwu.himusic.fragment.PlayFooterFragment;
import com.avenwu.himusic.service.PlayService;
import com.avenwu.himusic.manager.Logger;
import com.avenwu.himusic.manager.UIHelper;
import com.avenwu.himusic.widget.ZoomOutPageTransformer;

import java.util.Locale;

public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener, ServiceConnection {
    private final String TAG = HomeActivity.class.getSimpleName();
    private PlayService mPlayService;
    private PlayFooterFragment mFooter;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private final int SETTING_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.default_widget_album);
        mFooter = (PlayFooterFragment) getSupportFragmentManager().findFragmentById(R.id.footer);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        Logger.d(TAG, "bind service");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                onSearchRequested();
                break;
            case R.id.action_about:
                UIHelper.toast(HomeActivity.this, "About");
                break;
            case R.id.action_settings:
                UIHelper.toast(HomeActivity.this, "Setting");
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTING_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTING_REQUEST_CODE:
                UIHelper.toast(this, "Setting success changed");
                break;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mPlayService = ((PlayService.PlayBinder) service).getService();
        mFooter.bindService(mPlayService);
        Logger.d(TAG, name.getClassName() + " connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.d(TAG, name.getClassName() + " disconnected");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        Logger.d(TAG, "unbind service");
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ArtistAlbumsFragment();
                case 1:
                    return new MusicListFragment();
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_artist).toUpperCase(l);
                case 1:
                    return getString(R.string.title_music).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_searchable, container, false);
            rootView.setBackgroundResource(android.R.drawable.sym_def_app_icon);
            return rootView;
        }
    }

}
