package com.maze.MazeApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

/**
 * View of the line the user draws with their finger.
 */
public class FingerLine extends View {
    public static Paint mPaint, ePaint;
    private Path mPath;
    private final int[][] solutionPath;
//    private boolean solvedMaze;
    public static boolean solvedMaze, inTrack = true;
    private int flow = 0;
    boolean mazeStarted = false;

    public FingerLine(Context context, AttributeSet attrs, int[][] solutionPath) {
        super(context, attrs);
        this.solutionPath = solutionPath;
        init();
    }

    private void init() {
        // Create the paint brush
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.path));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(10f);

        //DEBUG
        ePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ePaint.setStyle(Style.STROKE);
        ePaint.setColor(ContextCompat.getColor(getContext(), R.color.orange));
        ePaint.setStrokeCap(Paint.Cap.ROUND);
        ePaint.setStrokeJoin(Paint.Join.ROUND);
        ePaint.setStrokeWidth(2f);
        //DEBUG

        solvedMaze = false;
        mPath = new Path();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        if(solvedMaze || MainActivity.hackMode){
            canvas.drawLine((solutionPath[0][0] + solutionPath[0][2])/2, solutionPath[0][3],(solutionPath[0][0] + solutionPath[0][2])/2,
                    (solutionPath[0][1] + solutionPath[0][3])/2, ePaint );
            for (int i = 0; i < solutionPath.length - 1; i++) {
                canvas.drawLine((solutionPath[i][0] + solutionPath[i][2])/2,(solutionPath[i][1] + solutionPath[i][3])/2,
                        (solutionPath[i+1][0] + solutionPath[i+1][2])/2,(solutionPath[i+1][1] + solutionPath[i+1][3])/2, ePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean xInCell;
        boolean yInCell;
        xInCell = (event.getX() >= solutionPath[flow][0] && event.getX() <= solutionPath[flow][2]);
        yInCell = (event.getY() >= solutionPath[flow][1] && event.getY() <= solutionPath[flow][3]);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                inTrack = false;
                mPath.moveTo(event.getX(), event.getY());
                if (xInCell && yInCell) {
                    mPaint.setColor(ContextCompat.getColor(getContext(), R.color.path));
                    Utilities.sms.start();
                    mazeStarted = true;
                    inTrack = true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!solvedMaze) {
                    mPath.lineTo(event.getX(), event.getY());
                    if (xInCell && yInCell) {
                        if (flow == 0 && !mazeStarted){
                            Utilities.sms.start();
                            mazeStarted = true;
                            inTrack = true;
                        }
                        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.path));
//                        MainActivity.vibe.cancel();
                        inTrack = true;
                    } else if ((event.getX() >= solutionPath[flow + 1][0] && event.getX() <= solutionPath[flow + 1][2]) &&
                            (event.getY() >= solutionPath[flow + 1][1] && event.getY() <= solutionPath[flow + 1][3])) {
                        flow++;
                        inTrack = true;
                    } else {
//                        Utilities.Vibrate(new long[]{0, 100});
//                        Utilities.oot.start();
                        inTrack = false;
                    }
                    if (flow == solutionPath.length - 1) {
                        solvedMaze = true;
                        MainActivity.util.startGameSolvedAnimation();
                        Toast.makeText(this.getContext(), "Well Done!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                inTrack = true;
//                MainActivity.vibe.cancel();
                if(solvedMaze){
                    MainActivity.util.createMaze();
                } else if (mazeStarted){
                    MainActivity.util.playAudioWithDelay(Utilities.levelFailed, 800);
                    Utilities.fb.start();
                    flow = 0;
                    mazeStarted = false;
                } else{
                    flow = 0;
                    mazeStarted = false;
                }
                break;
            default:
                return false;
        }
        // Schedule a repaint
        invalidate();

        return true;
    }
}