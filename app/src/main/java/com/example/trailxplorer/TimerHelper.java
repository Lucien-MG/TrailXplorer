package com.example.trailxplorer;

import android.os.Handler;
import android.widget.TextView;

public class TimerHelper {
    TextView counterView;

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
    }

    public void start() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void stop() {
        timerHandler.removeCallbacks(timerRunnable);
    }
}
