package com.ab15.seekbarandclock;


import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView tvSeekValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        seekBar = findViewById(R.id.seekBar);
        tvSeekValue = findViewById(R.id.tvSeekValue);

        // Set initial value display
        tvSeekValue.setText("Value: " + seekBar.getProgress());

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update text when progress changes
                tvSeekValue.setText("Value: " + progress);

                // Optional: you can do something with the value here
                // Example: change text color based on value
                if (progress < 30) {
                    tvSeekValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (progress < 70) {
                    tvSeekValue.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    tvSeekValue.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: called when user starts touching
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: called when user releases
            }
        });
    }
}