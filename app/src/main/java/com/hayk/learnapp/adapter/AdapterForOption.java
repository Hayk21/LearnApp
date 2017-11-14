package com.hayk.learnapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayk.learnapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.11.2017.
 */

public class AdapterForOption extends RecyclerView.Adapter<AdapterForOption.ViewHolder> {
    private Context context;
    private List<OptionItem> list;
    private OnOptionAdapterItemClickListener onOptionAdapterItemClickListener;

    public AdapterForOption(Context context)

    {
        this.context = context;
        list = new ArrayList<>();
        list.add(new OptionItem(R.drawable.users,context.getString(R.string.users)));
        list.add(new OptionItem(R.drawable.image_placeholder,context.getString(R.string.page2)));
        list.add(new OptionItem(R.drawable.image_placeholder,context.getString(R.string.page2)));
    }

    @Override
    public AdapterForOption.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterForOption.ViewHolder holder, int position) {
        holder.setOnOptionViewHolderListener(new ViewHolder.onOptionViewHolderItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if(onOptionAdapterItemClickListener != null){
                    onOptionAdapterItemClickListener.onItemClicked(position);
                }
            }
        });
        Picasso.with(context).load(list.get(position).getIcon()).into(holder.icon);
        holder.name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnOptionAdapterItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnOptionAdapterListener(OnOptionAdapterItemClickListener onOptionAdapterListener) {
        this.onOptionAdapterItemClickListener = onOptionAdapterListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        AdapterForOption.ViewHolder.onOptionViewHolderItemClickListener optionViewHolderItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.option_icon);
            name = (TextView) itemView.findViewById(R.id.option_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (optionViewHolderItemClickListener != null) {
                        optionViewHolderItemClickListener.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }

        interface onOptionViewHolderItemClickListener {
            void onItemClicked(int position);
        }

        void setOnOptionViewHolderListener(AdapterForOption.ViewHolder.onOptionViewHolderItemClickListener optionViewHolderListener) {
            this.optionViewHolderItemClickListener =optionViewHolderListener;
        }
    }
}
