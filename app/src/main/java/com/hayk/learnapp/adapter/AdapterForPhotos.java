package com.hayk.learnapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hayk.learnapp.R;
import com.hayk.learnapp.rest.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 13.11.2017.
 */

public class AdapterForPhotos extends RecyclerView.Adapter<AdapterForPhotos.ViewHolder>{
    private Context context;
    private List<Photo> list;
//    OnPhotoAdapterItemClickListener adapterItemClickListener;


    public AdapterForPhotos(Context context, List<Photo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(list.get(position).getUrl() != null) {
            Picasso.with(context).load(list.get(position).getUrl()).error(R.mipmap.default_image).into(holder.photo, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        }else {
            Picasso.with(context).load(list.get(position).getThumbnailUrl()).error(R.mipmap.default_image).into(holder.photo, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
////        holder.setOnPhotoViewHolderListener(new ViewHolder.OnPhotoViewHolderItemClickListener() {
////            @Override
////            public void onItemClicked(int position) {
////                if(adapterItemClickListener != null){
////                    adapterItemClickListener.onItemClicked(list.get(position));
////                }
////            }
////        });
//    }

//    public interface OnPhotoAdapterItemClickListener{
//        void onItemClicked(Photo photo);
//    }
//
//    public void setOnAdapterListener(OnPhotoAdapterItemClickListener adapterListener){
//        this.adapterItemClickListener = adapterListener;
//    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        ProgressBar progressBar;
//        OnPhotoViewHolderItemClickListener viewHolderItemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo_place);
            progressBar = itemView.findViewById(R.id.image_load_progress);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(viewHolderItemClickListener != null){
//                        viewHolderItemClickListener.onItemClicked(getAdapterPosition());
//                    }
//                }
//            });
        }

//        interface OnPhotoViewHolderItemClickListener{
//            void onItemClicked(int position);
//        }
//
//        void setOnPhotoViewHolderListener(OnPhotoViewHolderItemClickListener viewHolderListener){
//            this.viewHolderItemClickListener = viewHolderListener;
//        }
    }
}
