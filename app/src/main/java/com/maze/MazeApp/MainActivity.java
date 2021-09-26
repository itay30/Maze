package com.maze.MazeApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public enum Color {WHITE, GRAY, BLACK}
    public int mazeSize, Level;
    public final int PADDING = 64, FAT_FINGERS_MARGIN = 0;
    public static int timesSolved = 0;
    public static boolean hackMode = false;
    public MazeView mMazeView;
    public FingerLine mFingerLine;
    public ImageView star, arrow;
    public static Vibrator vibe;
    public static MediaPlayer nms, ws, sms, levelOne, levelTwo, levelThree, levelFour, levelFive, levelSix, levelSeven;
    FrameLayout mFrameLayout;
    DisplayMetrics displaymetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart)
            startActivity(new Intent(MainActivity.this, InstructionClass.class)); //where we want to be while sound instructions

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // keeps screen awake

        final TextView current_level = findViewById(R.id.current_level);
        final TextView mazes_solved = findViewById(R.id.times_solved);

        nms = MediaPlayer.create(this, R.raw.newmazesound);
        sms = MediaPlayer.create(this, R.raw.startmazesound);
        ws = MediaPlayer.create(this, R.raw.winningsound);
        levelOne = MediaPlayer.create(this, R.raw.levelone);
        levelTwo = MediaPlayer.create(this, R.raw.leveltwo);
        levelThree = MediaPlayer.create(this, R.raw.levelthree);
        levelFour = MediaPlayer.create(this, R.raw.levelfour);
        levelFive = MediaPlayer.create(this, R.raw.levelfive);
        levelSix = MediaPlayer.create(this, R.raw.levelsix);
        levelSeven = MediaPlayer.create(this, R.raw.levelseven);

        // Get instance of Vibrator from current Context
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        this.getDisplay().getRealMetrics(displaymetrics);

        mFrameLayout = (FrameLayout) findViewById(R.id.mazeWrapper);
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.height = (int) Math.floor(displaymetrics.heightPixels * 0.7);
        mFrameLayout.setLayoutParams(params);

        findViewById(R.id.coordinatorLayout).setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    createMaze();
                    nms.start();
                    current_level.setText("Level: " + Level);
                    mazes_solved.setText("Mazes Solved: " + timesSolved);
                    if(timesSolved % 3 == 0){
                        switch (Level){
                            case(2):
                                playAudioWithDelay(levelTwo, 900);
                                break;
                            case(3):
                                playAudioWithDelay(levelThree, 900);
                                break;
                            case(4):
                                playAudioWithDelay(levelFour, 900);
                                break;
                            case(5):
                                playAudioWithDelay(levelFive, 900);
                                break;
                            case(6):
                                playAudioWithDelay(levelSix, 900);
                                break;
                            case(7):
                                playAudioWithDelay(levelSeven, 900);
                                break;
                        }
                    }
                    return super.onDoubleTap(e);
                }
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    Log.d("TEST", "onLongPress");
//                    if (!hackMode) {
//                        hackMode = true;
//                        Toast.makeText(MainActivity.this, "Hack Mode ON", Toast.LENGTH_SHORT).show();
//                        mazes_solved.setVisibility(View.VISIBLE);
//                    } else {
//                        hackMode = false;
//                        Toast.makeText(MainActivity.this, "Hack Mode OFF", Toast.LENGTH_SHORT).show();
//                        mazes_solved.setVisibility(View.INVISIBLE);
//                    }
//                }

                // implement here other callback methods like onFling, onScroll as necessary

//            @Override
//            public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
//                boolean result = false;
//                float diffY = moveEvent.getY() - downEvent.getY();
//                float diffX = moveEvent.getX() - downEvent.getX();
//
//                if(Math.abs(diffX) > Math.abs(diffY)){
//                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
//                        if (diffX > 0 ){
//                            onSwipeRight();
//                        }else{
//                            onSwipeLeft();
//                        }
//                        result = true;
//                    }
//                }
//                return result;
//            }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.current_level).setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    Log.d("TEST", "onLongPress");
                    if (!hackMode) {
                        hackMode = true;
                        Toast.makeText(MainActivity.this, "Hack Mode ON", Toast.LENGTH_SHORT).show();
                        mazes_solved.setVisibility(View.VISIBLE);
                    } else {
                        hackMode = false;
                        Toast.makeText(MainActivity.this, "Hack Mode OFF", Toast.LENGTH_SHORT).show();
                        mazes_solved.setVisibility(View.INVISIBLE);
                    }
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        createMaze();
        playAudioWithDelay(levelOne, 1000);
    } // on create end

    public void createMaze() {
        // First remove any existing MazeView and FingerLine
        if (mMazeView != null) {
            ((ViewGroup) mMazeView.getParent()).removeView(mMazeView);
        }
        if (mFingerLine != null) {
            ((ViewGroup) mFingerLine.getParent()).removeView(mFingerLine);
        }
        mazeSize = 7;
        mMazeView = new MazeView(this, mazeSize);

        timesSolved = Math.min(timesSolved, 20);
        Level = (timesSolved/3) + 1;
        int pathLevel = ((timesSolved/3)*5) + 20;
        while (mMazeView.lengthOfSolutionPath > pathLevel || mMazeView.lengthOfSolutionPath < pathLevel - 5){
            mMazeView = new MazeView(this, mazeSize);
        }
        Log.d("lengthh", "length of the maze " + mMazeView.lengthOfSolutionPath);
        Log.d("lengthh", "times solved maze " + timesSolved);
        Log.d("lengthh", "formula " + pathLevel);

        // Trace the path from start to farthestVertex using the line of predecessors,
        // apply this information to form an array of rectangles
        // which will be passed on to fingerLine view
        // where the line has to pass.
        // The array be checked against the drawn line in FingerLine.

        int[][] solutionAreas = new int[mMazeView.lengthOfSolutionPath][4];

        int currentVertexKey;
        int totalMazeWidth = displaymetrics.widthPixels - PADDING;
        // int totalMazeHeight = totalMazeWidth;
        int cellSide = totalMazeWidth / mazeSize;
        int row, column;
        int topLeftX, topLeftY, bottomRightX, bottomRightY;

        for (int i = 0; i < mMazeView.lengthOfSolutionPath; i++) {

            currentVertexKey = mMazeView.listOfSolutionVertexesKeys[i];

            // Translate vertex key to location on screen
            row = (currentVertexKey) / mazeSize;
            column = (currentVertexKey) % mazeSize;
            topLeftX = (PADDING / 2) + (column * cellSide) - FAT_FINGERS_MARGIN;
            topLeftY = (PADDING / 2) + (row * cellSide) - FAT_FINGERS_MARGIN;

            bottomRightX = (PADDING / 2) + ((column + 1) * cellSide) + FAT_FINGERS_MARGIN;
            bottomRightY = (PADDING / 2) + ((row + 1) * cellSide) + FAT_FINGERS_MARGIN;
            solutionAreas[i] = new int[]{ topLeftX, topLeftY, bottomRightX, bottomRightY };
        }

        mFrameLayout.addView(mMazeView);
        mFingerLine = new FingerLine(this, null, solutionAreas);
        mFrameLayout.addView(mFingerLine);

        // Add start arrow pic
        int startCellArrowX = solutionAreas[0][2]/2 - 10;
        int startCellArrowY = solutionAreas[0][3];
        arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setX(startCellArrowX);
        arrow.setY(startCellArrowY);
        arrow.setVisibility(View.VISIBLE);

        // Add star pic
        int endCellStrawberryX = (solutionAreas[mMazeView.lengthOfSolutionPath - 1][0]) + 15;
        int endCellStrawberryY = (solutionAreas[mMazeView.lengthOfSolutionPath - 1][1]) + 18;
        star = (ImageView) findViewById(R.id.star);
        star.setX(endCellStrawberryX);
        star.setY(endCellStrawberryY);
        star.setVisibility(View.VISIBLE);
    }


    public static void startGameSolvedAnimation() {
        ws.start();
        Vibrate(new long[]{0,150, 100, 2000, 20000}); // TA-DAMMMM Vibrate then 20 seconds sleep
        timesSolved++;
//        createMazeWithDelay();
    }

    public static void Vibrate (long[] pattern){
        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
        vibe.vibrate(pattern, 0);
    }

    public void playAudioWithDelay (MediaPlayer mediaPlayer, int delay){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }, delay);
    }

//    public static void createMazeWithDelay(){
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                findViewById(R.id.coordinatorLayout).performClick();
//            }
//        }, 900);
//    }

    // Function to remove a specific Integer from Integer array
    // and return a new Integer array without the specified value
    public static Integer[] removeValueFromArray(Integer[] array, Integer value) {
        ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(array));
        arrayList.remove(value);
        Integer[] newArray = new Integer[arrayList.size()];
        arrayList.toArray(newArray);
        return newArray;
    }


//    private void onSwipeRight() {
//        Log.d("TEST", "onSwipeRight");
//        FingerLine.ePaint.setColor(ContextCompat.getColor((this), R.color.orange));
//    }//change difficulty
//
//    private void onSwipeLeft() {
//        Log.d("TEST", "onSwipeLeft");
//        FingerLine.ePaint.setColor(ContextCompat.getColor((this), R.color.windowBackground));
//    }//change difficulty

//    public static void VibrateWin (){
//        long[] pattern = {0,100,200,100,100,100,100,100,200,100,500,100,225,100,600};
//        // The '0' here means to repeat indefinitely
//        // '0' is actually the index at which the pattern keeps repeating from (the start)
//        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
//        vibe.vibrate(pattern, 0);
//    }

}