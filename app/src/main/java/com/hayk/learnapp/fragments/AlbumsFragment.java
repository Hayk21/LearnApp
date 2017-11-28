package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hayk.learnapp.R;
import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.adapter.AdapterForAlbums;
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.Utils;


public class AlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView albumsList;
    private SwipeRefreshLayout refresh;
    private OnCurrentFragmentChangedListener currentFragmentChanged;
    private AdapterForAlbums adapterForAlbums;
    private DatabaseUpdatedReceiver databaseUpdatedReceiver = new DatabaseUpdatedReceiver();

    public static AlbumsFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_FOR_USER_ID,id);

        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentFragmentChanged = (OnCurrentFragmentChangedListener)context;
        currentFragmentChanged.onFragmentAttach("Albums");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(databaseUpdatedReceiver,new IntentFilter(DBFunctions.ALBUM_UPDATED_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(databaseUpdatedReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        currentFragmentChanged.onFragmentDetach();
        currentFragmentChanged = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View view){
        refresh = view.findViewById(R.id.swiperefresh);
        albumsList = view.findViewById(R.id.albums_list);
        albumsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterForAlbums = new AdapterForAlbums(getActivity(),null);
        albumsList.setAdapter(adapterForAlbums);
        getLoaderManager().initLoader(0,null,AlbumsFragment.this);
//        new AlbumsGeting().execute();


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.getInstance(getActivity()).getConnectivity()) {
                    DBFunctions.getInstance(getActivity()).updateAlbums(getArguments().getString(MainActivity.KEY_FOR_USER_ID));
                }else {
                    refresh.setRefreshing(false);
                    Toast.makeText(getActivity(), "Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new AlbumsLoader(getActivity(),getArguments().getString(MainActivity.KEY_FOR_USER_ID));
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        adapterForAlbums.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

//    private class AlbumsGeting extends AsyncTask{
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            return DBFunctions.getInstance(getActivity()).getData(DBHelper.ALBUM_TABLE,getArguments().getString(MainActivity.KEY_FOR_USER_ID));
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            albumsList.setLayoutManager(new LinearLayoutManager(getActivity()));
//            albumsList.setAdapter(adapterForAlbums);
//            adapterForAlbums.updateList((List<Album>) o);
//        }
//    }

    private class DatabaseUpdatedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            refresh.setRefreshing(false);
            getLoaderManager().getLoader(0).forceLoad();
        }
    }

    static private class AlbumsLoader extends CursorLoader{
        private String userId;

        public AlbumsLoader(Context context,String userId) {
            super(context);
            this.userId = userId;
        }

        @Override
        public Cursor loadInBackground() {
            return DBFunctions.getInstance(getContext()).getAlbumsCursor(userId);
        }
    }
}
