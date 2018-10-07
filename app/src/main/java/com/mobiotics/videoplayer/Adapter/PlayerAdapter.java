package com.mobiotics.videoplayer.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobiotics.videoplayer.R;
import com.mobiotics.videoplayer.Getter.VideoGetter;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.VideoViewHolder> {
    //this context we will use to inflate the layout
    private Context context;

    //we are storing all the products in a list
    private List<VideoGetter> videoGetterList;

    //getting the context and product list with constructor
    public PlayerAdapter(Context context, List<VideoGetter> productList) {
        this.context = context;
        this.videoGetterList = productList;
    }

    @Override
    public PlayerAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_playerview, null);
        return new PlayerAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.VideoViewHolder holder, int position) {
        //getting the product of the specified position
        VideoGetter videoGetter = videoGetterList.get(position);
        //binding the data with the viewholder views
        holder.mPlayertvTitle.setText(videoGetter.getTitle());
        holder.mPlayertvDisp.setText(videoGetter.getDescription());
        Glide.with(context).load(videoGetter.getThumb()).into(holder.mPlayerivThumb);

        holder.mPlayerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return videoGetterList.size();
    }


    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView  mPlayertvTitle;
        TextView  mPlayertvDisp;
        ImageView mPlayerivThumb;
        CardView  mPlayerCardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mPlayerivThumb   = itemView.findViewById(R.id._ivthumb);
            mPlayertvTitle   = itemView.findViewById(R.id._tvTitle);
            mPlayertvDisp    = itemView.findViewById(R.id._tvDiscription);
            mPlayerCardView = itemView.findViewById(R.id._cardview);

        }
    }
}
