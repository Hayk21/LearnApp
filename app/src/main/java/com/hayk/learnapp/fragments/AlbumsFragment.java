package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayk.learnapp.R;
import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.adapter.AdapterForAlbums;
import com.hayk.learnapp.application.AppController;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.rest.Album;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;


public class AlbumsFragment extends Fragment {

    private RecyclerView albumsList;
    private SwipeRefreshLayout refresh;
    private OnCurrentFragmentChangedListener currentFragmentChanged;
    private AdapterForAlbums adapterForAlbums;

    public static AlbumsFragment newInstance(int id) {

        Bundle args = new Bundle();
        args.putInt(MainActivity.KEY_FOR_USER_ID,id);

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
        adapterForAlbums = new AdapterForAlbums(getActivity());
        getAlbums();


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlbums();
            }
        });
    }

    private void getAlbums(){
        Call<List<Album>> albums = AppController.getServerAPI().getAlbums(getArguments().getInt(MainActivity.KEY_FOR_USER_ID));

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Response<List<Album>> response) {
                albumsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                albumsList.setAdapter(adapterForAlbums);
                adapterForAlbums.updateList(response.body());
                refresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
