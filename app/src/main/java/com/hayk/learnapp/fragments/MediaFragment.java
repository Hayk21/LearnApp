package com.hayk.learnapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayk.learnapp.R;
import com.hayk.learnapp.adapter.AdapterForMedias;
import com.hayk.learnapp.adapter.MediaItem;
import com.hayk.learnapp.interfaces.OnCurrentFragmentChangedListener;
import com.hayk.learnapp.other.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


public class MediaFragment extends Fragment {
    public static final String MEDIA_FRAGMENT_UNBIND = "MediaFragmentUnbind";
    private AdapterForMedias adapterForMedias;
    private OnCurrentFragmentChangedListener currentFragmentChangedListener;
    private OnMediaPlayerListener mediaPlayerItemClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentFragmentChangedListener = (OnCurrentFragmentChangedListener) context;
        if (currentFragmentChangedListener != null) {
            currentFragmentChangedListener.onFragmentAttach("Media");
        }
        mediaPlayerItemClickListener = (OnMediaPlayerListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setListeners();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(adapterForMedias);
        mediaPlayerItemClickListener.mediaItemClicked(MEDIA_FRAGMENT_UNBIND, null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (currentFragmentChangedListener != null) {
            currentFragmentChangedListener.onFragmentDetach();
            currentFragmentChangedListener = null;
        }
        mediaPlayerItemClickListener = null;
    }

    private void init(final View view) {
        createDir();
        RecyclerView mediaList = view.findViewById(R.id.media_list);
        mediaList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterForMedias = new AdapterForMedias(getActivity());
        EventBus.getDefault().register(adapterForMedias);
        mediaList.setAdapter(adapterForMedias);
    }

    private void setListeners() {
        adapterForMedias.setOnMediaAdapterItemClickListener(new AdapterForMedias.OnMediaAdapterItemClickListener() {
            @Override
            public void mediaItemClicked(String action, MediaItem mediaItem) {
                mediaPlayerItemClickListener.mediaItemClicked(action, mediaItem);
            }
        });
    }

    public void createDir() {
        File mediaFolder = new File(Utils.getInstance(getActivity()).getMediaFolderPath());
        if (!mediaFolder.exists()) {
            mediaFolder.mkdir();
        }
    }

    public interface OnMediaPlayerListener {
        void mediaItemClicked(String action, MediaItem mediaItem);
    }
}
