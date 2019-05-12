package com.example.trailxplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraphView extends View {

    private Paint paint;
    private Paint textPaint;

    private static boolean night;

    private static ArrayList<Long> speeds = new ArrayList<Long>();

    public GraphView(Context context) {
        super(context);
        init(null, 0);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //Paint instance for drawing the lines.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5f);

        //Paint instance for drawing the text.
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(5f);
        textPaint.setTextSize(100f);

        //Setting the style depending on the activation of the night mode.
        if (night) {
            setBackgroundColor(0xFF101922);
            paint.setColor(0xFFFCFCFC);
            textPaint.setColor(0xFFFCFCFC);
        }
        else {
            setBackgroundColor(0xFFFFFFFF);
            paint.setColor(0xFF808080);
            textPaint.setColor(0xFF808080);
        }
    }

    //Setter for the night boolean.
    public static void setNight(boolean test) {
        night = test;
    }

    //Setter for the list of points for the graph.
    public static void setList(ArrayList<Long> tmp) {
        speeds = tmp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Create the background rectangle.
        canvas.drawLine(100, 50, getWidth() - 100, 50, paint);
        canvas.drawLine(100, 50, 100, getHeight() - 100, paint);
        canvas.drawLine(100, getHeight() - 100, getWidth() - 100, getHeight() - 100, paint);
        canvas.drawLine(getWidth() - 100, 50, getWidth() - 100, getHeight() - 100, paint);

        //Create the axis.
        canvas.drawLine(150, getHeight() - 150, getWidth() - 150, getHeight() - 150, paint);
        canvas.drawLine(150, getHeight() - 150, 150, 100, paint);

        //Create the axis arrows.
        canvas.drawLine(getWidth() - 150, getHeight() - 150, getWidth() - 175, getHeight() - 125,paint);
        canvas.drawLine(getWidth() - 150, getHeight() - 150, getWidth() - 175, getHeight() - 175, paint);
        canvas.drawLine(150, 100, 125, 125, paint);
        canvas.drawLine(150, 100, 175, 125, paint);

        //Defining the axis lengths.
        int xaxis = getWidth() - 300;
        int yaxis = getHeight() - 300;

        //Defining the number of points needed.
        int nbpoints = speeds.size();

        if (nbpoints > 1) {

            //Defining the axis units.
            int xunit = xaxis / nbpoints;
            int yunit = yaxis / 10;

            for (int j = 0; j < 10; j++) {
                //Creating the marks at each unit on the y-axis.
                canvas.drawLine(150, j * yunit + 150, 162, j * yunit + 150, paint);
            }

            int i = 0;
            int gap = 1 + (nbpoints / 10);

            while (i < nbpoints) {

                //Creating the crosses corresponding to the points of the run.
                canvas.drawLine(i * xunit + 125, (10 - speeds.get(i)) * yunit + 150, i * xunit + 175, (10 - speeds.get(i)) * yunit + 150, paint);
                canvas.drawLine(i * xunit + 150, (10 - speeds.get(i)) * yunit + 125, i * xunit + 150, (10 - speeds.get(i)) * yunit + 175, paint);

                //Creating the marks at each unit on the x-axis.
                canvas.drawLine(i * xunit + 150, getHeight() - 150, i * xunit + 150, getHeight() - 162, paint);

                if (i > 0) {
                    //Creating the lines between the crosses.
                    canvas.drawLine((i - gap) * xunit + 150, (10 - speeds.get(i - gap)) * yunit + 150, i * xunit + 150, (10 - speeds.get(i)) * yunit + 150, paint);
                }

                i += gap;
            }
        }

        else {
            //Case where there are no speed recorded (run was too short).
            canvas.drawText("Nothing to show", 200, getHeight() / 2f, textPaint);
        }
    }
}
