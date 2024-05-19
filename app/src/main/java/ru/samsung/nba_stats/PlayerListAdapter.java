package ru.samsung.nba_stats;
// PlayerListAdapter

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.MyViewHolder>{
    Context context;

    ArrayList<PlayerInRoster> playersArrayList;
    public PlayerListAdapter(Context context, ArrayList<PlayerInRoster> playersArrayList) {

        this.context = context;
        this.playersArrayList = playersArrayList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.player_list_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlayerInRoster player = playersArrayList.get(position);
        Glide.with(context).load(player.playerHeadshot).into(holder.playerImage);
        holder.playerName.setText(player.playerName);
    }

    @Override
    public int getItemCount() {
        return playersArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView playerImage;
        TextView playerName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playerImage = itemView.findViewById(R.id.roster_player_image);
            playerName = itemView.findViewById(R.id.roster_player_name);
        }
    }
}
