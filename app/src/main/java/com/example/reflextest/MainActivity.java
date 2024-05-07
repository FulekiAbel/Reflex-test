package com.example.reflextest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // set the background on the screen
            relativeLayout.setBackgroundResource(R.color.green);

            final long time = System.currentTimeMillis();

            // function when stop button is clicked
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long time1 = System.currentTimeMillis();

                    long reflexTime = time1 - time;

                    // display reflex time in toast message
                    Toast.makeText(getApplicationContext(), "Your reflexes takes " + reflexTime + " time to work", Toast.LENGTH_LONG).show();

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

        mFirestore = FirebaseFirestore.getInstance();

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
                startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
            }
        });
    }
    private void saveReflexTimeToFirestore(long newReflexTime) {
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
                            userRef.update("reflexTime", newReflexTime)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Reflex time updated successfully");
                                        // Add the reflex time to the collection if it's better
                                        addReflexTimeToLeaderboard(userId, newReflexTime);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating reflex time", e);
                                    });
                        } else {
                            Log.d(TAG, "Current reflex time is better, no update needed");

                            // Add the reflex time to the collection if it's better
                            addReflexTimeToLeaderboard(userId, newReflexTime);
                        }
                    } else {
                        // User document doesn't exist, create a new one with the new reflex time
                        Map<String, Object> reflexTimeData = new HashMap<>();
                        reflexTimeData.put("reflexTime", newReflexTime);
                        userRef.set(reflexTimeData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Reflex time added successfully");

                                    addReflexTimeToLeaderboard(userId, newReflexTime);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error adding reflex time", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting user document", e);
                });
    }

    private void addReflexTimeToLeaderboard(String userId, long newReflexTime) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Reference to the document containing the user's reflex time in the leaderboard
        DocumentReference leaderboardRef = mFirestore.collection("Leaderboard").document(userId);

        // Update or add the reflex time to the leaderboard
        leaderboardRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User's data exists in the leaderboard, update the reflex time if it's better
                        Long currentReflexTime = documentSnapshot.getLong("reflexTime");
                        if (currentReflexTime == null || newReflexTime < currentReflexTime) {
                            // Either there's no reflex time stored yet or the new reflex time is better
                            leaderboardRef.update("reflexTime", newReflexTime)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Reflex time updated in leaderboard successfully");
                                        Toast.makeText(getApplicationContext(), "Ez az eddigi legjobb időd!", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating reflex time in leaderboard", e);
                                    });
                        } else {
                            Log.d(TAG, "Current reflex time is better in leaderboard, no update needed");
                            Toast.makeText(getApplicationContext(), "Ennél már volt gyorsabb!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // User's data doesn't exist in the leaderboard, add the reflex time
                        Map<String, Object> reflexTimeData = new HashMap<>();
                        reflexTimeData.put("userEmail", userEmail);
                        reflexTimeData.put("reflexTime", newReflexTime);
                        leaderboardRef.set(reflexTimeData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Reflex time added to leaderboard successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error adding reflex time to leaderboard", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting user's data from leaderboard", e);
                });
    }
}