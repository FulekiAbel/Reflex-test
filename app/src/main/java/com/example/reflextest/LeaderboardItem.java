package com.example.reflextest;

public class LeaderboardItem {
    private String userEmail;
    private long reflexTime;

    public LeaderboardItem(String userEmail, long reflexTime) {
        this.userEmail = userEmail;
        this.reflexTime = reflexTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public long getReflexTime() {
        return reflexTime;
    }
}
