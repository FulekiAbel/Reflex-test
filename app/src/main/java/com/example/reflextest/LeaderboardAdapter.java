package com.example.reflextest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mLeaderboardList;

    public LeaderboardAdapter(Context context, List<String> leaderboardList) {
        super(context, 0, leaderboardList);
        mContext = context;
        mLeaderboardList = leaderboardList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String leaderboardEntry = mLeaderboardList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(leaderboardEntry);

        return convertView;
    }
}

