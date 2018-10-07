package com.mobiotics.videoplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobiotics.videoplayer.Activity.VideoPlayerActivity;
import com.mobiotics.videoplayer.R;
import com.mobiotics.videoplayer.Getter.VideoGetter;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    //this context we will use to inflate the layout
    private Context context;

    //we are storing all the products in a list
    private List<VideoGetter> videoGetterList;

    //getting the context and product list with constructor
    public VideoAdapter(Context context, List<VideoGetter> productList) {
        this.context = context;
        this.videoGetterList = productList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_videoview, null);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        //getting the product of the specified position
        VideoGetter videoGetter = videoGetterList.get(position);
       //binding the data with the viewholder views
        holder.tvTitle.setText(videoGetter.getTitle());
        holder.tvDisp.setText(videoGetter.getDescription());
        Glide.with(context).load(videoGetter.getThumb()).into(holder.ivThumb);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("title"      ,videoGetterList.get(position).getTitle()); //you can name the keys whatever you like
                intent.putExtra("discription",videoGetterList.get(position).getDescription());
                intent.putExtra("videourl"   ,videoGetterList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return videoGetterList.size();
    }


    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView   tvTitle;
        TextView   tvDisp;
        ImageView  ivThumb;
        CardView   mCardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ivThumb   = itemView.findViewById(R.id.ivthumb);
            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvDisp    = itemView.findViewById(R.id.tvDiscription);
            mCardView = itemView.findViewById(R.id.cardview);

        }
    }
}
