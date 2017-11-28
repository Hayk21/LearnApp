package com.hayk.learnapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.database.DBHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 13.11.2017.
 */

public class AdapterForPhotos extends CursorRecyclerViewAdapter<AdapterForPhotos.ViewHolder>{
    private Context context;
//    OnPhotoAdapterItemClickListener adapterItemClickListener;


    public AdapterForPhotos(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_card,parent,false);
        return new ViewHolder(view);
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

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        if(cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL)) != null) {
            Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_THUMB_URL))).into(viewHolder.photo);
        }else {
            Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_URL))).into(viewHolder.photo);
        }
    }

//    public interface OnPhotoAdapterItemClickListener{
//        void onItemClicked(Photo photo);
//    }
//
//    public void setOnAdapterListener(OnPhotoAdapterItemClickListener adapterListener){
//        this.adapterItemClickListener = adapterListener;
//    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
//        OnPhotoViewHolderItemClickListener viewHolderItemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.photo_place);

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
