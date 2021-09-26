package com.maze.MazeApp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class InstructionClass extends AppCompatActivity {
    private Timer timer;
    private int i = 0;
    public static MediaPlayer si;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_class);

        si = MediaPlayer.create(this, R.raw.startinstructions);
        si.start();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

        timer = new Timer();
        int Period = 430;
        int delay = 0;
        timer.schedule( new TimerTask() {
            @SuppressLint("SetTextI18n")
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void run() {
                if(i < 100) {
                    i++;
                } else {
                    timer.cancel();
                    startActivity(new Intent(InstructionClass.this, MainActivity.class));
                    finish();
                }
            }
        }, delay, Period );
    }
}