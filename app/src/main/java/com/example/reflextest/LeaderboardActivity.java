package com.example.reflextest;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = "LeaderboardActivity";

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardItem> leaderboardList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardList = new ArrayList<>();
        adapter = new LeaderboardAdapter(this, leaderboardList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        retrieveLeaderboardData();
    }

    private void retrieveLeaderboardData() {
        // Query Firestore to retrieve leaderboard data
        firestore.collection("Leaderboard")
                .orderBy("reflexTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String userEmail = document.getString("userEmail");
                        long reflexTime = document.getLong("reflexTime");

                        // Create a LeaderboardItem object and add it to the list
                        LeaderboardItem item = new LeaderboardItem(userEmail, reflexTime);
                        leaderboardList.add(item);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving leaderboard data: ", e);
                });
    }
}
