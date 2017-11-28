package com.hayk.learnapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.database.DBFunctions;
import com.hayk.learnapp.database.DBHelper;

/**
 * Created by User on 13.11.2017.
 */

public class AdapterForAlbums extends CursorRecyclerViewAdapter<AdapterForAlbums.ViewHolder> {
    private Context context;
//    private OnAlbumAdapterItemClickListener adapterItemClickListener;


    public AdapterForAlbums(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card,parent,false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
////        holder.setOnAlbumViewHolderListener(new ViewHolder.OnAlbumViewHolderItemClickListener() {
////            @Override
////            public void onItemClicked(int position) {
////                if(adapterItemClickListener != null){
////                    adapterItemClickListener.onItemClicked(list.get(position));
////                }
////            }
////        });
//
////        Call<List<Photo>> photos = AppController.getServerAPI().getPhotos(list.get(position).getID());
////
////        photos.enqueue(new Callback<List<Photo>>() {
////            @Override
////            public void onResponse(Response<List<Photo>> response) {
////                AdapterForPhotos adapterForPhotos = new AdapterForPhotos(context);
////                holder.photosList.setAdapter(adapterForPhotos);
////                adapterForPhotos.updateList(response.body());
////            }
////
////            @Override
////            public void onFailure(Throwable t) {
////                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
////            }
////        });
//    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(DBHelper.ALBUM_TITLE)));
        viewHolder.photosList.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        viewHolder.photosList.setAdapter(new AdapterForPhotos(context,DBFunctions.getInstance(context).getPhotosCursor(cursor.getString(cursor.getColumnIndex(DBHelper.ID)))));
    }

//    public interface OnAlbumAdapterItemClickListener{
//        void onItemClicked(Album album);
//    }
//
//    public void setOnAdapterListener(OnAlbumAdapterItemClickListener adapterListener){
//        this.adapterItemClickListener = adapterListener;
//    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        RecyclerView photosList;
//        OnAlbumViewHolderItemClickListener viewHolderItemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.album_title);
            photosList =  itemView.findViewById(R.id.photos_list);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(viewHolderItemClickListener != null){
//                        viewHolderItemClickListener.onItemClicked(getAdapterPosition());
//                    }
//                }
//            });
        }

//        interface OnAlbumViewHolderItemClickListener{
//            void onItemClicked(int position);
//        }
//
//        void setOnAlbumViewHolderListener(OnAlbumViewHolderItemClickListener viewHolderListener){
//            this.viewHolderItemClickListener = viewHolderListener;
//        }
    }
}