package com.example.trailxplorer;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerHelper {
    TextView counterView;
    Button startButton;
    Button stopButton;
    Button pauseButton;

    long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;

            counterView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    public TimerHelper(TextView viewer) {
        counterView = viewer;
        //startButton = start;
        //pauseButton = pause;

    }

    public void start() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void stop() {
        timerHandler.removeCallbacks(timerRunnable);
    }

}
