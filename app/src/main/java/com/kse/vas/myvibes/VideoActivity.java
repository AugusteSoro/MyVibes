package com.kse.vas.myvibes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoViewLecture;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        videoViewLecture = findViewById(R.id.videoViewLecture);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.i("url", url);
        videoViewLecture.setVideoPath(url);
        videoViewLecture.start();
        // Set the media controller buttons
        MediaController mediaController = new MediaController(VideoActivity.this);
        videoViewLecture.setMediaController(mediaController);

        // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoViewLecture);


            // Set MediaController for VideoView
            videoViewLecture.setMediaController(mediaController);


    }

    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoViewLecture.getCurrentPosition());
        videoViewLecture.pause();
    }


    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        videoViewLecture.seekTo(position);
    }

}
