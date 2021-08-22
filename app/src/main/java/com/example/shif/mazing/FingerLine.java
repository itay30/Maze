package com.example.shif.mazing;

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

import java.util.ArrayList;

/**
 * View of the line the user draws with their finger.
 */
public class FingerLine extends View {
    private Paint mPaint, sPaint, ePaint;
    private Path mPath;
    private int[][] solutionPath;
    private boolean solvedMaze;
    private int flow = 0;

//    public FingerLine(Context context) {
//        this(context, null);
//        init();
//    }
//
//    public FingerLine(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }

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

        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setStyle(Style.STROKE);
        sPaint.setColor(ContextCompat.getColor(getContext(), R.color.purple));
        sPaint.setStrokeCap(Paint.Cap.ROUND);
        sPaint.setStrokeJoin(Paint.Join.ROUND);
        sPaint.setStrokeWidth(20f);

        //DEBUG
        ePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ePaint.setStyle(Style.STROKE);
        ePaint.setColor(ContextCompat.getColor(getContext(), R.color.windowBackground));
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
        for (int i = 0; i < solutionPath.length; i++) {
            canvas.drawRect(solutionPath[i][0],solutionPath[i][1],solutionPath[i][2],solutionPath[i][3],ePaint);
            canvas.drawPoint((solutionPath[i][0] + solutionPath[i][2])/2,(solutionPath[i][1] + solutionPath[i][3])/2,sPaint);
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
                mPath.moveTo(event.getX(), event.getY());
                if (xInCell && yInCell) {
                    mPaint.setColor(ContextCompat.getColor(getContext(), R.color.path));
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                if (xInCell && yInCell) {
                    mPaint.setColor(ContextCompat.getColor(getContext(), R.color.path));
                } else if ((event.getX() >= solutionPath[flow+1][0] && event.getX() <= solutionPath[flow+1][2]) &&
                        (event.getY() >= solutionPath[flow+1][1] && event.getY() <= solutionPath[flow+1][3])){
                    flow++;
                } else {
                    mPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
                    ePaint.setColor(ContextCompat.getColor(getContext(), R.color.orange));
                }
                if(flow == solutionPath.length - 1 && !solvedMaze){
                    MainActivity.startGameSolvedAnimation();
                    Toast.makeText(this.getContext(), R.string.maze_solved, Toast.LENGTH_SHORT).show();
                    solvedMaze = true;
                    flow = 0;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flow = 0;
                break;
            default:
                return false;
        }
        // Schedule a repaint
        invalidate();

//        // Check if user solved the maze
//        boolean xInCellx;
//        boolean yInCelly;
//
//        for (int i = 0; i < solutionPath.length; i++) {
//
//            xInCellx = (event.getX() >= solutionPath[i][0] && event.getX() <= solutionPath[i][2]);
//            yInCelly = (event.getY() >= solutionPath[i][1] && event.getY() <= solutionPath[i][3]);
//
//            if (xInCellx && yInCelly) {
//                solved.set(i, true);
////                Log.d("checkmaze", "" + solved.get(i) + " -ok");
//
//                if (!solved.contains(false) && !solvedMaze) {
//                    MainActivity.startGameSolvedAnimation();
//                    Toast.makeText(this.getContext(), R.string.maze_solved, Toast.LENGTH_SHORT).show();
//                    solvedMaze = true;
//                    return true; // not sure it this line is necessary
//                }
//            }
//        }
        return true;
    }
}