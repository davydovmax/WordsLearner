package com.example.WordsLearner.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.example.WordsLearner.R;
import com.example.WordsLearner.db.WordsLearnerDataHelper;
import com.example.WordsLearner.model.Word;
import com.example.WordsLearner.utils.Utils;

import java.io.File;
import java.io.IOException;

public class RecordSoundActivity extends Activity {

    private final static String LOG_TAG = "RecordSound";

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private String imageName;
    private String soundName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sound);

        imageName = getIntent().getStringExtra(Word.WORD_PHOTO_EXTRA);
        soundName = changeExtention(imageName);

        Button recordBtn = (Button)findViewById(R.id.btn_record);
        Button listenBtn = (Button)findViewById(R.id.btn_listen);
        Button saveBtn = (Button)findViewById(R.id.btn_save);

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                }
                return true;
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordsLearnerDataHelper db = new WordsLearnerDataHelper(RecordSoundActivity.this);
                db.addWord(new Word(imageName, soundName, null));
                finish();
            }
        });
    }

    private String changeExtention(String name) {
        String filenameArray[] = name.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        return name.replace("." + extension, ".mp3");
    }

    private void startRecording() {
        Utils.checkDirectory(Utils.SOUNDS_FOLDER);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Utils.SOUNDS_FOLDER + File.separator + soundName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(Utils.SOUNDS_FOLDER + File.separator + soundName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

}