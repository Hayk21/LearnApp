package com.hayk.learnapp.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hayk.learnapp.R;
import com.hayk.learnapp.application.ApplicationClass;
import com.hayk.learnapp.rest.Album;
import com.hayk.learnapp.rest.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by User on 13.11.2017.
 */

public class AdapterForAlbums extends RecyclerView.Adapter<AdapterForAlbums.ViewHolder> {
    private Context context;
    private List<Album> list;
    private OnAlbumAdapterItemClickListener adapterItemClickListener;

    public AdapterForAlbums(Context context){
        this.context = context;
        list = new ArrayList<>();
    }

    public void addItems(List<Album> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setOnAlbumViewHolderListener(new ViewHolder.OnAlbumViewHolderItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if(adapterItemClickListener != null){
                    adapterItemClickListener.onItemClicked(list.get(position));
                }
            }
        });
        holder.title.setText(list.get(position).getTitle());
        holder.photosList.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        Call<List<Photo>> photos = ((ApplicationClass)context.getApplicationContext()).getServerAPI().getPhotos(list.get(position).getId());

        photos.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Response<List<Photo>> response) {
                AdapterForPhotos adapterForPhotos = new AdapterForPhotos(context);
                holder.photosList.setAdapter(adapterForPhotos);
                adapterForPhotos.addItems(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnAlbumAdapterItemClickListener{
        void onItemClicked(Album album);
    }

    public void setOnAdapterListener(OnAlbumAdapterItemClickListener adapterListener){
        this.adapterItemClickListener = adapterListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        RecyclerView photosList;
        OnAlbumViewHolderItemClickListener viewHolderItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.album_title);
            photosList = (RecyclerView) itemView.findViewById(R.id.photos_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderItemClickListener != null){
                        viewHolderItemClickListener.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }

        interface OnAlbumViewHolderItemClickListener{
            void onItemClicked(int position);
        }

        void setOnAlbumViewHolderListener(OnAlbumViewHolderItemClickListener viewHolderListener){
            this.viewHolderItemClickListener = viewHolderListener;
        }
    }
}