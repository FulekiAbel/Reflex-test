package com.example.reflextest;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public Button button1, button2;
    public RelativeLayout relativeLayout;

    // runnable function
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // set the background on the screen
            relativeLayout.setBackgroundResource(R.color.green);

            // get the system time in milli second
            // when the screen background is set
            final long time = System.currentTimeMillis();

            // function when stop button is clicked
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get the system time in milli second
                    // when the stop button is clicked
                    long time1 = System.currentTimeMillis();

                    // display reflex time in toast message
                    Toast.makeText(getApplicationContext(), "Your reflexes takes " + (time1 - time) + " time to work", Toast.LENGTH_LONG).show();

                    // remove the background again
                    relativeLayout.setBackgroundResource(0);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.rlVar1);
        button1 = findViewById(R.id.btVar1);
        button2 = findViewById(R.id.btVar2);

        // function when the start button is clicked
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // generate a random number from 1-10
                Random random = new Random();
                int num = random.nextInt(10);

                // call the runnable function after
                // a post delay of num seconds
                Handler handler = new Handler();
                handler.postDelayed(runnable, num * 1000);
            }
        });
    }
}