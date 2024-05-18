package ru.samsung.nba_stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class GameScoresAdapter extends RecyclerView.Adapter<GameScoresAdapter.MyViewHolder>{
    Context context;

    ArrayList<Game> gamesArrayList;
    public GameScoresAdapter(Context context, ArrayList<Game> gamesArrayList) {

        this.context = context;
        this.gamesArrayList = gamesArrayList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Game game = gamesArrayList.get(position);
        Glide.with(context).load(game.teamImage1).into(holder.teamImage1);
        Glide.with(context).load(game.teamImage2).into(holder.teamImage2);
        holder.teamName1.setText(String.valueOf(game.teamName1));
        holder.teamName2.setText(String.valueOf(game.teamName2));
        holder.score1.setText(String.valueOf(game.score1));
        holder.score2.setText(String.valueOf(game.score2));
    }

    @Override
    public int getItemCount() {
        return gamesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView teamImage1,teamImage2;
        TextView teamName1,teamName2,score1,score2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            teamImage1 = itemView.findViewById(R.id.team_image_1);
            teamImage2 = itemView.findViewById(R.id.team_image_2);
            teamName1 = itemView.findViewById(R.id.team_name_1);
            teamName2 = itemView.findViewById(R.id.team_name_2);
            score1 = itemView.findViewById(R.id.score1);
            score2 = itemView.findViewById(R.id.score2);
        }
    }
}
