package com.example.reflextest;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insertData(String name, int score) {
        Map<String, Object> player = new HashMap<>();
        player.put("name", name);
        player.put("score", score);

        db.collection("leaderboard")
                .add(player)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Player added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding player", e));
    }

    // Code to retrieve data from the leaderboard
    public void getLeaderboard() {
        db.collection("leaderboard")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> leaderboardList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        long score = document.getLong("score");
                        leaderboardList.add(name + ": " + score);
                    }
                    // Update UI with leaderboard data
                    updateUI(leaderboardList);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error getting leaderboard", e));
    }

    // Displaying the leaderboard data in a RecyclerView
    private void updateUI(List<String> leaderboardList) {
        // Initialize the ListView
        ListView leaderboardListView = findViewById(R.id.leaderboardListView);

        // Create a new LeaderboardAdapter with the leaderboardList data
        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(this, leaderboardList);

        // Set the adapter for the ListView
        leaderboardListView.setAdapter(leaderboardAdapter);
    }
}
