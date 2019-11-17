package com.example.movie;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie.adapter.ItemCategoryAdapter;
import com.example.movie.contact.ItemCategory;
import com.example.movie.contact.Video;
import com.example.movie.define.DefineURL;
import com.example.movie.define.Define_Methods;
import com.example.movie.inter_face.IOnClickVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;

public class PlayMovie extends AppCompatActivity {
    private static final String TAG = "StartVideo";
    String url = DefineURL.ITEM_CATEGORY_URL;
    ArrayList<ItemCategory> list;
    ItemCategoryAdapter adapter;
    VideoView videoView;
    RecyclerView recyclerView;

    SeekBar seekBar;
    ImageView btFull, btSmall, btPause, btPlay, btNext, btPrevious;
    RelativeLayout layout, controlPlay;
    TextView tvTimeEnd, tvTimeStart;
    ItemCategory itemPlaying;




    int getTimeCurrent;
    int position = 0;

    Switch autoPlay;
    Handler handler = new Handler();
    ActionBar actionBar;
    WindowManager windowManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        //Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        //Video
        videoView = findViewById(R.id.vv);

        //Time Video Play
        tvTimeStart=findViewById(R.id.tvTimeBegin);
        tvTimeEnd=findViewById(R.id.tvTimeFinish);

        //Next List
        autoPlay = findViewById(R.id.swAuto);
        recyclerView = findViewById(R.id.rvPlayVideo);
        layout = findViewById(R.id.rlVideo);
        list = new ArrayList<>();

        //See Bar
        seekBar = findViewById(R.id.seekBar);
        btFull = findViewById(R.id.btFull);
        btSmall = findViewById(R.id.btSmall);
        btSmall.setVisibility(View.INVISIBLE);

        //Controller Play
        controlPlay = findViewById(R.id.controller);
        btPause = findViewById(R.id.btPause);
        btPlay = findViewById(R.id.btPlay);
        btNext = findViewById(R.id.btNext);
        btPrevious = findViewById(R.id.btPre);
        btPlay.setVisibility(View.INVISIBLE);

        //Start Video
        new getData(url).execute();
        String urlPlay = getIntent().getStringExtra("url");
        Uri uri = Uri.parse(urlPlay);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // setup SeekBar
        ShowController_within_5s showController_delay = new ShowController_within_5s();
        handler.postDelayed(showController_delay, 5000);

        //getSeeBarStatus
        getSeeBarStatus();

        //Set Time
        tvTimeStart.setText(millisecondsToString(0));
        tvTimeEnd.setText(millisecondsToString(videoView.getDuration()));

        //Play
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.getCurrentPosition() == videoView.getDuration()) {
                    videoView.seekTo(videoView.getCurrentPosition());
                }
                videoView.start();
                btPlay.setVisibility(View.INVISIBLE);
                btPause.setVisibility(View.VISIBLE);
            }
        });

        //Pause
        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                btPause.setVisibility(View.INVISIBLE);
                btPlay.setVisibility(View.VISIBLE);
            }
        });

        //Button Next Video
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlay.setVisibility(View.INVISIBLE);
                btPause.setVisibility(View.VISIBLE);
                ItemCategory item = list.get(0);
                if (position >= 0) {
                    Uri uri = Uri.parse(item.getUrl());
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();

                }
                list.remove(0);
                list.add(item);
                position++;
                itemPlaying = item;
            }
        });

        //Button Previous Video
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlay.setVisibility(View.INVISIBLE);
                btPause.setVisibility(View.VISIBLE);
                if (position > 0) {
                    ItemCategory item = list.get(list.size() - 2);
                    Uri uri = Uri.parse(item.getUrl());
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();
                    position--;
                    itemPlaying = item;
                } else position = 0;
            }
        });

        //Full Screen
        btFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeCurrent = videoView.getCurrentPosition();
                btFull.setVisibility(View.INVISIBLE);
                btSmall.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params1.width = params.MATCH_PARENT;
                params1.height = params1.MATCH_PARENT;
                layout.setLayoutParams(params);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                videoView.seekTo(getTimeCurrent);
            }
        });

        //Exist Full Screen
        btSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btFull.setVisibility(View.VISIBLE);
                btSmall.setVisibility(View.INVISIBLE);


                getTimeCurrent = videoView.getCurrentPosition();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params1.width = params.MATCH_PARENT;
                params1.height = 800;
                layout.setLayoutParams(params1);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                videoView.seekTo(getTimeCurrent);
            }
        });

        //Touch Video
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                controlPlay.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //autoPlay
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                adapter = new ItemCategoryAdapter(list, getBaseContext());
                if (autoPlay.isChecked() == true) {
                        if(list!=null)
                        {
                            ItemCategory itemNext;
                        }
                }
                else
                {

                }
            }
        });

    }

    // format time of video
    private String millisecondsToString(int milliseconds) {
        return new SimpleDateFormat("mm:ss").format(milliseconds);
    }

    //updating SeekBar
    public void getSeeBarStatus()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                    // mp is your MediaPlayer
                    // progress is your ProgressBar
                    int currentPosition = 0;
                    int total = videoView.getDuration();
                    seekBar.setMax(total);
                    while (videoView != null && currentPosition < total) {
                        try {
                            Thread.sleep(1000);
                            currentPosition = videoView.getCurrentPosition();
                        } catch (InterruptedException e) {
                            return;
                        }
                        seekBar.setProgress(currentPosition);

                    }
                }
            }).start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoView.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
//    private Runnable runnable =new Runnable() {
//        @Override
//        public void run() {
//            if(videoView!=null)
//            {
//                int currentPosition=videoView.getCurrentPosition()/1000;
//                seekBar.setProgress(currentPosition);
//                int timeCurrent=videoView.getCurrentPosition();
//                tvTimeStart.setText(millisecondsToString(timeCurrent));
//                int timeFinish=videoView.getDuration();
//                tvTimeEnd.setText(millisecondsToString(timeFinish));
//                updateSeeBar();
//            }
//        }
//    };
//
//    private void  updateSeeBar()
//    {
//        handler.postDelayed(runnable,1000);
//        seekBar.setMax(videoView.getDuration());
//    }

    //List Next Video
    class getData extends AsyncTask<Void, Void, Void> {
        String newurl;
        String json = "";

        public getData(String newurl) {
            this.newurl = newurl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(newurl);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                int character;
                while ((character = inputStream.read()) != -1) {
                    json += (char) character;
                }
                Log.d(TAG, "doInBackground: " + json);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String avt = object.getString("avatar");
                    String time = object.getString("date_created");
                    String name = object.getString("title");
                    String url = object.getString("file_mp4");
                    list.add(new ItemCategory(avt, name, time, url));
                }
                adapter = new ItemCategoryAdapter(list, getBaseContext());
                adapter.setClickVideo(new IOnClickVideo() {
                    @Override
                    public void playVideo(String url) {
                        Uri uri = Uri.parse(url);
                        videoView.setVideoURI(uri);
                        videoView.requestFocus();
                        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                        videoView.start();

                    }

                    @Override
                    public void putInSql(Video video) {
                    }
                });
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), 1, RecyclerView.VERTICAL, false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }

    // display seekBar within 5s
    class ShowController_within_5s implements Runnable
    {
        @Override
        public void run() {
            handler.postDelayed(this,5000);
            controlPlay.setVisibility(View.GONE);
        }
    }

}
