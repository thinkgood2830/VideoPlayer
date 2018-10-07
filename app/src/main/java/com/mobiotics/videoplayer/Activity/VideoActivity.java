package com.mobiotics.videoplayer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobiotics.videoplayer.Constants.Constants;
import com.mobiotics.videoplayer.R;
import com.mobiotics.videoplayer.Adapter.VideoAdapter;
import com.mobiotics.videoplayer.Getter.VideoGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private RecyclerView      mRecyclerView;
    private VideoAdapter mVideoAdapter;
    private List<VideoGetter> mVideoList;
    private ProgressDialog    mProgressDialog;
    private FirebaseAuth      mAuth;
    private FirebaseUser      muser;
    private ImageView         ivUserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mAuth = FirebaseAuth.getInstance();
        widgetIntlization();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignupActivity.class));
        }
    }

    private void widgetIntlization(){
        ivUserProfile = (ImageView)findViewById(R.id.iv_profile);
        ivUserProfile.setOnClickListener(listner);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVideoList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        loadVideoList();
    }

    View.OnClickListener listner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id==R.id.iv_profile){
                muser = mAuth.getCurrentUser();
                String name  = muser.getDisplayName();
                String email = muser.getEmail();
                showDialog(name, email);
            }
        }
    };

    private void showDialog(String name, String email){
        AlertDialog alertDialog = new AlertDialog.Builder(
                VideoActivity.this).create();
        alertDialog.setTitle("Logdd in");
        String sLogininfo = "Name : "+ name + "\n" +"Email : "+ email;
        alertDialog.setMessage(sLogininfo);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void loadVideoList() {
        mProgressDialog.setMessage("Loading Videos...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
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
                            }

                            mVideoAdapter = new VideoAdapter(getApplicationContext(),mVideoList);
                            mRecyclerView.setAdapter(mVideoAdapter);
                            mProgressDialog.dismiss();
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
}
