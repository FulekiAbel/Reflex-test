package com.example.reflextest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public Button button1, button2, leaderboardButton;
    public RelativeLayout relativeLayout;

    private DatabaseReference mDatabase;
    private FirebaseFirestore mFirestore;
    private CollectionReference mTimes;

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

                    // calculate reflex time
                    long reflexTime = time1 - time;

                    // display reflex time in toast message
                    Toast.makeText(getApplicationContext(), "Your reflexes takes " + reflexTime + " time to work", Toast.LENGTH_LONG).show();


                    mFirestore = FirebaseFirestore.getInstance();
                    mTimes = mFirestore.collection("Times");

                    // save reflex time to Firebase database
                    saveReflexTimeToFirestore(reflexTime);

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
        leaderboardButton = findViewById(R.id.btVar3);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
                // Start LeaderboardActivity when Leaderboard button is clicked
                startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
            }
        });
    }
    private void saveReflexTimeToFirestore(long newReflexTime) {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the document containing the user's reflex time
        DocumentReference userRef = mFirestore.collection("Users").document(userId);

        // Retrieve the current reflex time from Firestore
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User document exists, check if there's a reflex time stored
                        Long currentReflexTime = documentSnapshot.getLong("reflexTime");
                        if (currentReflexTime == null || newReflexTime < currentReflexTime) {
                            // Either there's no reflex time stored yet or the new reflex time is better
                            // Update the reflex time in Firestore
                            userRef.update("reflexTime", newReflexTime)
                                    .addOnSuccessListener(aVoid -> {
                                        // Reflex time updated successfully
                                        Log.d(TAG, "Reflex time updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error occurred while updating reflex time
                                        Log.w(TAG, "Error updating reflex time", e);
                                    });
                        } else {
                            // Current reflex time is better than the new one, no need to update
                            Log.d(TAG, "Current reflex time is better, no update needed");
                        }
                    } else {
                        // User document doesn't exist, create a new one with the new reflex time
                        Map<String, Object> reflexTimeData = new HashMap<>();
                        reflexTimeData.put("reflexTime", newReflexTime);
                        ((DocumentReference) userRef).set(reflexTimeData)
                                .addOnSuccessListener(aVoid -> {
                                    // Reflex time added successfully
                                    Log.d(TAG, "Reflex time added successfully");
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while adding reflex time
                                    Log.w(TAG, "Error adding reflex time", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while retrieving user document
                    Log.w(TAG, "Error getting user document", e);
                });
    }

    private void updateReflexTime(long reflexTime) {
        // Create a new HashMap to store reflex time data
        Map<String, Object> reflexTimeData = new HashMap<>();
        reflexTimeData.put("reflexTime", reflexTime);

        // Add or update reflex time data in Firestore
        mFirestore.collection("Times")
                .document("reflexTime")
                .set(reflexTimeData)
                .addOnSuccessListener(aVoid -> {
                    // Reflex time updated successfully
                    Log.d(TAG, "Reflex time updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while updating reflex time
                    Log.w(TAG, "Error updating reflex time", e);
                });
    }
    }