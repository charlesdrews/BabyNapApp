package com.charlesdrews.babynapapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {

    private Button noiseButton, resetButton;
    private Chronometer chronometer;
    private WhiteNoiseGenerator whiteNoiseGenerator;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noiseButton = findViewById(R.id.noise_button);
        noiseButton.setOnClickListener(view -> startAndStop());

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(view -> reset());

        chronometer = findViewById(R.id.chronometer);

        whiteNoiseGenerator = new WhiteNoiseGenerator();
    }

    private void startAndStop() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            whiteNoiseGenerator.play();
            chronometer.start();
        } else {
            whiteNoiseGenerator.stop();
            chronometer.stop();
        }
        noiseButton.setText(isPlaying ? "Stop" : "Start");
    }

    private void reset() {
        chronometer.setBase(SystemClock.elapsedRealtime());
    }
}
