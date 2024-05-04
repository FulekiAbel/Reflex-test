package com.example.reflextest;

public class ReflexTime {
    private long time;

    public ReflexTime() {
        // Default constructor required for calls to DataSnapshot.getValue(ReflexTime.class)
    }

    public ReflexTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
