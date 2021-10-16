package com.maze.MazeApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

        final Button skipButton = findViewById(R.id.skipBtn);

        si = MediaPlayer.create(this, R.raw.startinstructions1);
        si.start();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

        skipButton.setOnClickListener(v -> {
            si.stop();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        timer = new Timer();
        int Period = 570; // 57 seconds
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