package com.maze.MazeApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Random;

/**
 * This view displays the maze on screen.
 * It holds the graph that represents the maze,
 * and the arrays of vertical and horizontal walls.
 */
public class MazeView extends View {

    public static Paint mazePaint;
    public int screenWidth;
    public int screenHeight;
    public int lengthOfSolutionPath;
    int cellWidth;
    int cellHeight;
    int padding = 64;
    int mazeSize;
    public boolean[][] verticalLines;
    public boolean[][] horizontalLines;
    public Integer[] listOfSolutionVertexesKeys;
    DisplayMetrics displaymetrics;
    public Graph graph;
    public double fractionOfWallsToRemove = 0.15;

    public MazeView(Context context, int size) {
        super(context);
        this.mazeSize = size;
        this.graph = new Graph(size);
        init();
    }

    private void init() {
        // Create the paint brush
        mazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mazePaint.setColor(ContextCompat.getColor(getContext(), R.color.purple));
        mazePaint.setStyle(Paint.Style.STROKE);
        mazePaint.setStrokeCap(Paint.Cap.ROUND);
        mazePaint.setStrokeJoin(Paint.Join.ROUND);
        mazePaint.setStrokeWidth(12f);

        displaymetrics = new DisplayMetrics();
        getContext().getDisplay().getRealMetrics(displaymetrics);

        screenWidth = displaymetrics.widthPixels - padding;
        screenHeight = displaymetrics.heightPixels - padding;
        cellWidth = screenWidth / mazeSize;
        cellHeight = cellWidth;

        // Create 2 2-dimensional arrays to keep the vertical and the horizontal lines of the MazeView
        verticalLines = new boolean[mazeSize][mazeSize + 1];
        horizontalLines = new boolean[mazeSize + 1][mazeSize];

        for (int i = 0; i < mazeSize; i++) {
            Arrays.fill(verticalLines[i], Boolean.TRUE);
        }
        for (int j = 0; j < mazeSize + 1; j++) {
            Arrays.fill(horizontalLines[j], Boolean.TRUE);
        }
        // Break the starting wall of the maze
        horizontalLines[mazeSize][0] = false;

        // The maze starts at the left-bottom corner of the screen
        int graphStartKey = ( (Double) (graph.size - Math.sqrt(graph.size)) ).intValue();

        // Declare the first cell of the maze as visited
        graph.V[graphStartKey].visited = true;

        MazeBuilder.carveWay(graph, graph.V[graphStartKey], this);

        // Improvement of the maze: remove a few random walls
        // to make the maze more confusing.
        // Choose randomly a few walls and break those walls
        Random rand = new Random();
        for (int holes = 0; holes < Math.floor(Math.pow(fractionOfWallsToRemove * mazeSize, 2)); holes++) {

            int randomXvertical = rand.nextInt(mazeSize - 1);
            int randomYvertical = rand.nextInt(mazeSize - 1) + 1;
            verticalLines[randomXvertical][randomYvertical] = false;

            int randomXhorizontal = rand.nextInt(mazeSize - 1) + 1;
            int randomYhorizontal = rand.nextInt(mazeSize - 1);
            horizontalLines[randomXhorizontal][randomYhorizontal] = false;
        }

        MazeBuilder.removeEdges(this);

        listOfSolutionVertexesKeys = LongestPathFinder.findLongestPath(graph);

        lengthOfSolutionPath = listOfSolutionVertexesKeys.length;

        // Break the ending wall of the maze
        // get the end vertex key
        int endKey = listOfSolutionVertexesKeys[0];

        // check if it's vertical or horizontal
        boolean horizontalEnd = endKey < mazeSize || endKey >= mazeSize * (mazeSize - 1);
        if (horizontalEnd) {
            if (endKey < mazeSize) { // top row of horizontal lines
                horizontalLines[0][endKey] = false;
            } else { // bottom row of horizontal lines
                horizontalLines[mazeSize][endKey % mazeSize] = false;
            }
        } else {
            if (endKey % mazeSize == 0) { // left most column of vertical lines
                verticalLines[endKey / mazeSize][0] = false;
            } else { // right most column of vertical lines
                verticalLines[endKey / mazeSize][mazeSize] = false;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
