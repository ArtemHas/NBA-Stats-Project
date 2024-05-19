package ru.samsung.nba_stats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.MyViewHolder>{

    Context context;

    ArrayList<Team> teamsArrayList;
    SelectListener listener;
    public TeamListAdapter(Context context, ArrayList<Team> teamsArrayList, SelectListener listener) {

        this.context = context;
        this.teamsArrayList = teamsArrayList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.team_list_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Team team = teamsArrayList.get(position);
        Glide.with(context).load(team.teamImage).into(holder.teamImage);
        holder.teamName.setText(team.teamName);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(team);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView teamImage;
        TextView teamName;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            teamImage = itemView.findViewById(R.id.player_stats_team_image);
            teamName = itemView.findViewById(R.id.player_stats_team_name);
            cardView = itemView.findViewById(R.id.team_list_container);

        }
    }
}
