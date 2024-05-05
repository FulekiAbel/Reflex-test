package com.example.reflextest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private List<LeaderboardItem> leaderboardList;

    public LeaderboardAdapter(Context context, List<LeaderboardItem> leaderboardList) {
        this.context = context;
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardItem item = leaderboardList.get(position);

        holder.positionTextView.setText(String.valueOf(position + 1));
        holder.userEmailTextView.setText(item.getUserEmail());
        holder.reflexTimeTextView.setText(String.valueOf(item.getReflexTime()));
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userEmailTextView;
        TextView reflexTimeTextView;
        TextView positionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            positionTextView = itemView.findViewById(R.id.positionTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            reflexTimeTextView = itemView.findViewById(R.id.reflexTimeTextView);
        }
    }
}
