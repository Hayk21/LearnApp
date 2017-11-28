package com.hayk.learnapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.hayk.learnapp.other.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 14.11.2017.
 */

public class AdapterForContacts extends RecyclerView.Adapter<AdapterForContacts.ViewHolder> {
    private List<ContactObject> list;
    private Context context;
    private OnContactAdapterItemClickListener adapterItemClickListener;

    public AdapterForContacts(Context context){
        list = new ArrayList<>();
        this.context = context;
    }

    public void updateList(List<ContactObject> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnContactViewHolderListener(new ViewHolder.OnContactViewHolderItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if(adapterItemClickListener != null){
                    adapterItemClickListener.onItemClicked(list.get(position));
                }
            }
        });
        holder.userName.setText(list.get(position).getAllName());
        if(list.get(position).getImg() != null) {
            Picasso.with(context).load(list.get(position).getImg()).transform(new CircleTransform()).into(holder.img);
        }else {
            Picasso.with(context).load(R.drawable.human).transform(new CircleTransform()).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnContactAdapterItemClickListener{
        void onItemClicked(ContactObject contactObject);
    }

    public void setOnContactAdapterListener(OnContactAdapterItemClickListener adapterListener){
        this.adapterItemClickListener = adapterListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView userName;
        ImageView img;
        OnContactViewHolderItemClickListener viewHolderItemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.contact_name);
            img = itemView.findViewById(R.id.contact_img);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderItemClickListener != null){
                        viewHolderItemClickListener.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0,1,0,"Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    return false;
                }
            });
            contextMenu.add(0,1,0,"Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return false;
                }
            });
        }


        interface OnContactViewHolderItemClickListener{
            void onItemClicked(int position);
        }

        void setOnContactViewHolderListener(OnContactViewHolderItemClickListener viewHolderListener){
            this.viewHolderItemClickListener = viewHolderListener;
        }
    }
}
