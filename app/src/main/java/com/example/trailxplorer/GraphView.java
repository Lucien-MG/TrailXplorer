package com.example.trailxplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class GraphView extends View {

    private Paint paint;

    private int[] speeds = new int[]{0, 5, 7, 6, 3, 4, 7};

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
        //Set the background color.
        setBackgroundColor(Color.WHITE);

        //Paint instance for drawing the lines.
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);
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
        int yaxis = getHeight() - 250;

        //Defining the number of points needed.
        int nbpoints = speeds.length;

        //Defining the axis units.
        int xunit = xaxis / nbpoints;
        int yunit = yaxis / 10;

        for (int i = 0; i < nbpoints; i++) {
            canvas.drawLine(i * xunit + 125, (10 - speeds[i]) * yunit + 100, i * xunit + 175, (10 - speeds[i]) * yunit + 100, paint);
            canvas.drawLine(i * xunit + 150, (10 - speeds[i]) * yunit + 75, i * xunit + 150, (10 - speeds[i]) * yunit + 125, paint);

            if (i > 0) {
                canvas.drawLine((i - 1) * xunit + 150, (10 - speeds[i - 1]) * yunit + 100, i * xunit + 150, (10 - speeds[i]) * yunit + 100, paint);
            }
        }
    }
}