package com.mobiotics.videoplayer.Activity;


import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.mobiotics.videoplayer.Adapter.PlayerAdapter;
import com.mobiotics.videoplayer.Constants.Constants;
import com.mobiotics.videoplayer.R;
import com.mobiotics.videoplayer.DBHelper.SQLiteHelper;
import com.mobiotics.videoplayer.Getter.VideoGetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private TextView  mTvtitle;
    private TextView  mTvDiscription;
    private ImageView mIvLoading;
    private ImageView mIvArrow;
    private Switch   mSvautoplay;
    private String   mTitle;
    private String   mDiscription;
    private String   mUrl;
    private String   mId;
    private String   sDBTitle;
    private String   sDBLeftTime;
    private String   sNxtTitle;
    private String   sNxtUrl;
    private String   sNxtDisp;
    private String   sTitle;
    private RecyclerView mPRecyclerView;
    private PlayerAdapter mPlayerAdapter;
    private List<VideoGetter> mVideoList;
    private SQLiteHelper mSqLiteHelper;
    private int mTableRowCount;
    private MediaSource mediaSource;
    private int currentVideo = 0;
    private static DecimalFormat decimalFormat = new DecimalFormat(".##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoPlayerView);
        mTvtitle       = findViewById(R.id.tvPlayerTitle);
        mTvDiscription = findViewById(R.id.tvPlayerDiscription);
        mIvLoading     = findViewById(R.id.img_gif);
        mIvArrow       = findViewById(R.id.iv_arrow);
        mIvArrow.setTag("up_arrow.png");
        mIvArrow.setOnClickListener(listner);
        mPRecyclerView = findViewById(R.id.player_recyclerView);
        mSvautoplay    = findViewById(R.id.sb_autoplay);
        mPRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mPRecyclerView.setLayoutManager(llm);
        mSqLiteHelper = new SQLiteHelper(this);
        mVideoList = new ArrayList<>();

        mTitle         = getIntent().getExtras().getString("title");
        mDiscription   = getIntent().getExtras().getString("discription");
        mUrl           = getIntent().getExtras().getString("videourl");
        mTvtitle.setText(mTitle);
        mTvDiscription.setText(mDiscription);
        sTitle = mTitle;
        initiatePlayer(mUrl);
        loadVideoList();
        currentVideo = 0;

    }


    private void initiatePlayer(String Url){
        mTableRowCount = mSqLiteHelper.numberOfRows();
        try {
            if (mTableRowCount>0) {
                Cursor cursor = mSqLiteHelper.selectDB(mTitle);
                if (cursor.moveToFirst()) {
                    sDBTitle = cursor.getString(cursor.getColumnIndex("title"));
                    sDBLeftTime = cursor.getString(cursor.getColumnIndex("leftduration"));
                    Log.d(TAG, "Video Title : " + sDBTitle + "Left Time : " + sDBLeftTime);
                }
                cursor.close();
            }
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videoURI = Uri.parse(Url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);

            if (sDBTitle.equals(sTitle)){
                double num = Double.parseDouble(sDBLeftTime);
                exoPlayer.seekTo((long) (60000*num));
                sTitle = null;
            }else if (sDBTitle.equals(sNxtTitle)){
                long num = Long.valueOf(sDBLeftTime);
                exoPlayer.seekTo(60000*num);
            }
            exoPlayer.setPlayWhenReady(true);

        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }

        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: STATE_ENDED");
                        exoPlayer.release();
                        if (mSvautoplay.isChecked()){
                            Toast.makeText(VideoPlayerActivity.this,"AUTOPLAY ENABLED",Toast.LENGTH_LONG).show();
                            currentVideo++;
                            if (currentVideo > mVideoList.size() - 1) {
                                currentVideo = 0;
                            }
                            if (currentVideo==1){
                                currentVideo = currentVideo-1;
                                sNxtUrl   = mVideoList.get(currentVideo).getUrl();
                                sNxtTitle = mVideoList.get(currentVideo).getTitle();
                                sNxtDisp  = mVideoList.get(currentVideo).getDescription();
                                mTvtitle.setText(sNxtTitle);
                                mTvDiscription.setText(sNxtDisp);
                                initiatePlayer(sNxtUrl);
                                currentVideo =1;
                            }else {
                                sNxtUrl   = mVideoList.get(currentVideo).getUrl();
                                sNxtTitle = mVideoList.get(currentVideo).getTitle();
                                sNxtDisp  = mVideoList.get(currentVideo).getDescription();
                                mTvtitle.setText(sNxtTitle);
                                mTvDiscription.setText(sNxtDisp);
                                initiatePlayer(sNxtUrl);
                            }
                        }

                        break;
                    case ExoPlayer.STATE_IDLE:
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.d(TAG, "onPlayerStateChanged: STATE_READY");
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    private void loadVideoList() {
        loadGIF(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.VIDEO_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                VideoGetter mVideoGetter = new VideoGetter(
                                        obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("thumb"),
                                        obj.getString("description"),
                                        obj.getString("url")
                                );
                                mVideoList.add(mVideoGetter);
                                for(VideoGetter videoList : mVideoList){
                                    if(videoList.getTitle().equals(mTitle)){
                                        mVideoList.remove(videoList);
                                    }
                                }
                            }

                            mPlayerAdapter = new PlayerAdapter(getApplicationContext(),mVideoList);
                            mPRecyclerView.setAdapter(mPlayerAdapter);
                            loadGIF(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String mDuration = "" + (exoPlayer.getDuration() / 1000) + "";
        String mcurrent  = "" + (exoPlayer.getCurrentPosition() / 1000) + "";
        String mLeft     = "" + ((exoPlayer.getDuration() - exoPlayer.getCurrentPosition()) / 1000) + "".trim();

        double fduration = Double.parseDouble(mDuration);
        fduration = fduration/60;
        double fcurrent  = Double.parseDouble(mcurrent);
        fcurrent = fcurrent/60;
        double fleft1     = fduration - fcurrent;
        double fleft      = Double.parseDouble(mLeft);
        fleft = fleft/60;
        double dTwodigit = Double.parseDouble(decimalFormat.format(fleft));
        mLeft = String.valueOf(dTwodigit);
        Log.d(TAG, "onDestroy: fduration    :"+fduration);
        Log.d(TAG, "onDestroy: fcurrent     :"+fcurrent);
        Log.d(TAG, "onDestroy: fleft        :"+fleft);
        Log.d(TAG, "onDestroy: dTwodigit    :"+dTwodigit);
        Log.d(TAG, "onDestroy: mLeft         :"+mLeft);

        if (mTableRowCount>0) {
            Cursor cursor = mSqLiteHelper.selectDB(mTitle);
            if (cursor.moveToFirst()) {
                sDBTitle = cursor.getString(cursor.getColumnIndex("title"));
                sDBLeftTime = cursor.getString(cursor.getColumnIndex("leftduration"));
                Log.d(TAG, "Video Title : " + sDBTitle + "Left Time : " + sDBLeftTime);
                if (sDBTitle.equals(mTitle)) {
                    mSqLiteHelper.updateDB(mTitle,mLeft);
                }
            }else{
                mSqLiteHelper.addDB(mTitle,mLeft);
            }
            cursor.close();
        }else{
            mSqLiteHelper.addDB(mTitle,mLeft);

        }
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void releasePlayer(){
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
        }
    }

    private void loadGIF(boolean flag){
        if (flag==true){
            mIvLoading.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mIvLoading);
            Glide.with(this).load(R.raw.loading).into(imageViewTarget);
        }else{
            mIvLoading.setVisibility(View.GONE);
        }
    }

    View.OnClickListener listner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id==R.id.iv_arrow){
                String imgtag = (String)mIvArrow.getTag();
                if (imgtag.equals("up_arrow.png")){
                    mTvDiscription.setVisibility(View.VISIBLE);
                    mIvArrow.setBackgroundResource(R.drawable.down_arrow);
                    mIvArrow.setTag("down_arrow.png");
                }else if (imgtag.equals("down_arrow.png")){
                    mTvDiscription.setVisibility(View.GONE);
                    mIvArrow.setBackgroundResource(R.drawable.up_arrow);
                    mIvArrow.setTag("up_arrow.png");
                }
            }
        }
    };

}
