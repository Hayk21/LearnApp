package com.hayk.learnapp.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.other.Utils;
import com.hayk.learnapp.services.MediaPlayerService;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 28.11.2017.
 */

public class AdapterForMedias extends RecyclerView.Adapter<AdapterForMedias.ViewHolder> {

    private Context context;
    private List<MediaItem> mediaList;
    private OnMediaAdapterItemClickListener mediaAdapterItemClickListener;
    private ImageView currentPresedButton;
    private View oldView;
    private String action;
    public static final String PLAY_ACTION = "PlayAction";
    public static final String PAUSE_ACTION = "PauseAction";

    public AdapterForMedias(Context context) {
        this.context = context;
        mediaList = new ArrayList<>();
        updateList(getMedias());
    }

    private void updateList(List<MediaItem> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (MediaPlayerService.getIsPlaying() && mediaList.get(position).getPath().equals(MediaPlayerService.getCurrentFile())) {
            currentPresedButton = holder.playOrPause;
            currentPresedButton.setVisibility(View.VISIBLE);
            oldView = currentPresedButton;
            currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_file_icon));
            action = PLAY_ACTION;
        }
        holder.name.setText(mediaList.get(position).getName());
        if (mediaList.get(position).isMusic()) {
            Picasso.with(context).load(R.drawable.music_icon).into(holder.mediaIcon);
        } else {
            Picasso.with(context).load(R.drawable.video_icon).into(holder.mediaIcon);
        }
        setAdapterListeners(holder, position);
    }

    private void setAdapterListeners(final ViewHolder holder, final int position) {
        holder.setOnViewHolderItemClickListener(new ViewHolder.OnViewHolderItemClickListener() {
            @Override
            public void viewHolderClicked(View view) {
                action = "";
                switch (view.getId()) {
                    case R.id.media_card:
                        if (oldView != null) {
                            oldView.findViewById(R.id.play_file).setVisibility(View.INVISIBLE);
                            holder.playOrPause.setVisibility(View.VISIBLE);
                            oldView = holder.itemView;
                        } else {
                            holder.playOrPause.setVisibility(View.VISIBLE);
                            oldView = holder.itemView;
                        }
                        break;
                    case R.id.play_file:
                        if (currentPresedButton != null) {
                            if (!currentPresedButton.equals(holder.playOrPause)) {
                                currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_file_icon));
                                currentPresedButton = holder.playOrPause;
                                currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_file_icon));
                                action = PLAY_ACTION;
                            } else {
                                if (!MediaPlayerService.getIsPlaying()) {
                                    currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_file_icon));
                                    action = PLAY_ACTION;
                                } else {
                                    currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_file_icon));
                                    action = PAUSE_ACTION;
                                }
                            }
                        } else {
                            currentPresedButton = holder.playOrPause;
                            currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_file_icon));
                            action = PLAY_ACTION;
                        }
                        break;
                }
                mediaAdapterItemClickListener.mediaItemClicked(action, mediaList.get(position));
            }
        });
    }

    public void setOnMediaAdapterItemClickListener(OnMediaAdapterItemClickListener mediaAdapterItemClickListener) {
        this.mediaAdapterItemClickListener = mediaAdapterItemClickListener;
    }

    public interface OnMediaAdapterItemClickListener {
        void mediaItemClicked(String action, MediaItem mediaItem);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Subscribe
    public void getEvent(String action) {
        switch (action) {
            case MediaPlayerService.MEDIA_FINISHED:
                currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_file_icon));
                break;
            case MediaPlayerService.MEDIA_PLAY:
                currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_file_icon));
                break;
            case MediaPlayerService.MEDIA_PAUSE:
                currentPresedButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_file_icon));
                break;
        }
    }

    private List<MediaItem> getMedias() {
        List<MediaItem> mediaItemList = new ArrayList<>();
        File mediaFolder = new File(Utils.getInstance(context).getMediaFolderPath());
        MediaMetadataRetriever mediaData = new MediaMetadataRetriever();
        byte[] rawArt;
        boolean isMelody;
        for (int i = 0; i < mediaFolder.listFiles().length; i++) {

            mediaData.setDataSource(mediaFolder.listFiles()[i].toString());
            rawArt = mediaData.getEmbeddedPicture();

            isMelody = isMelody(mediaFolder.listFiles()[i].toString());
            mediaItemList.add(new MediaItem(mediaFolder.listFiles()[i].getName(), rawArt, isMelody));
        }
        return mediaItemList;
    }

    private boolean isMelody(String fileName) {
        boolean result = false;
        String filenameArray[] = fileName.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        switch (extension) {
            case "mp3":
                result = true;
                break;
            case "mp4":
                result = false;
        }
        return result;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaIcon, playOrPause;
        TextView name;
        OnViewHolderItemClickListener viewHolderItemClickListener;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolderItemClickListener.viewHolderClicked(view);
            }
        };

        ViewHolder(View itemView) {
            super(itemView);
            mediaIcon = itemView.findViewById(R.id.media_icon);
            playOrPause = itemView.findViewById(R.id.play_file);
            name = itemView.findViewById(R.id.media_name);

            itemView.setOnClickListener(onClickListener);
            playOrPause.setOnClickListener(onClickListener);
        }

        void setOnViewHolderItemClickListener(OnViewHolderItemClickListener viewHolderItemClickListener) {
            this.viewHolderItemClickListener = viewHolderItemClickListener;
        }

        interface OnViewHolderItemClickListener {
            void viewHolderClicked(View view);
        }
    }
}
