package com.hayk.learnapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.database.DBHelper;

/**
 * Created by User on 10.11.2017.
 */

public class AdapterForUsers extends CursorRecyclerViewAdapter<AdapterForUsers.ViewHolder> {
    private onAdapterItemClickListener adapterItemClickListener;

    public AdapterForUsers(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_card,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        viewHolder.setOnViewHolderListener(new ViewHolder.onViewHolderItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if(adapterItemClickListener != null){
                    adapterItemClickListener.onItemClicked(cursor.getString(cursor.getColumnIndex(DBHelper.ID)));
                }}
        });
        viewHolder.userName.setText(cursor.getString(cursor.getColumnIndex(DBHelper.USER_NICK_NAME)));
        viewHolder.userEmail.setText(cursor.getString(cursor.getColumnIndex(DBHelper.USER_EMAIL)));
    }

    public interface onAdapterItemClickListener{
        void onItemClicked(String id);
    }

    public void setOnAdapterListener(AdapterForUsers.onAdapterItemClickListener adapterListener){
        this.adapterItemClickListener = adapterListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userEmail;
        onViewHolderItemClickListener viewHolderItemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username_place);
            userEmail = itemView.findViewById(R.id.useremail_place);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderItemClickListener != null){
                        viewHolderItemClickListener.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }

        interface onViewHolderItemClickListener{
            void onItemClicked(int position);
        }

        void setOnViewHolderListener(AdapterForUsers.ViewHolder.onViewHolderItemClickListener viewHolderListener){
            this.viewHolderItemClickListener = viewHolderListener;
        }
    }
}
