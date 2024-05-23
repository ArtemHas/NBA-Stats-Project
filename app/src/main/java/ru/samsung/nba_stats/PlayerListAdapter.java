package ru.samsung.nba_stats;
// PlayerListAdapter

import android.annotation.SuppressLint;
import android.content.Context;
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

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.MyViewHolder>{
    Context context;

    ArrayList<PlayerInRosterForRecyclerView> playersArrayList;
    private SelectListenerRoster listener;
    public PlayerListAdapter(Context context, ArrayList<PlayerInRosterForRecyclerView> playersArrayList, SelectListenerRoster listener) {
        this.listener = listener;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlayerInRosterForRecyclerView player = playersArrayList.get(position);
        Glide.with(context).load(player.playerHeadshot).into(holder.playerImage);
        holder.playerName.setText(player.playerName);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(player);
            }
        });

    }

    @Override
    public int getItemCount() {
        return playersArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView playerImage;
        TextView playerName;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.roster_container);
            playerImage = itemView.findViewById(R.id.roster_player_image);
            playerName = itemView.findViewById(R.id.roster_player_name);
        }
    }
}
