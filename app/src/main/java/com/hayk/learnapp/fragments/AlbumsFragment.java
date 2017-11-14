package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hayk.learnapp.R;
import com.hayk.learnapp.activitys.MainActivity;
import com.hayk.learnapp.adapter.AdapterForAlbums;
import com.hayk.learnapp.application.ApplicationClass;
import com.hayk.learnapp.rest.Album;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;


public class AlbumsFragment extends Fragment {

    RecyclerView albumsList;
    RecyclerView.LayoutManager manager;
    AdapterForAlbums adapterForAlbums;


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
        albumsList = (RecyclerView)view.findViewById(R.id.albums_list);
        manager = new LinearLayoutManager(getActivity());
        adapterForAlbums = new AdapterForAlbums(getActivity());

        Call<List<Album>> albums = ((ApplicationClass)getActivity().getApplication()).getServerAPI().getAlbums(getArguments().getInt(MainActivity.KEY_FOR_USER_ID));

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Response<List<Album>> response) {
                albumsList.setLayoutManager(manager);
                albumsList.setAdapter(adapterForAlbums);
                adapterForAlbums.addItems(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), "Oups", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
