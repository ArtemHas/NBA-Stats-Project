package ru.samsung.nba_stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        /*new FetchImage(game.teamImage1, game.teamImage2, holder).start();*/

        holder.teamName1.setText(String.valueOf(game.teamName1));
        holder.teamName2.setText(String.valueOf(game.teamName2));
        holder.score1.setText(String.valueOf(game.score1));
        holder.score2.setText(String.valueOf(game.score2));
        holder.teamImage1.setImageBitmap(game.teamImage1);
        holder.teamImage2.setImageBitmap(game.teamImage2);
       /* holder.teamImage1.setImageResource(game.teamImage1);
        holder.teamName1.setText(game.teamName1);
        ЗДЕСЬ УЖЕ ГОТОВОЕ ВСЕ ЗАКИНУТЬ*/
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
/*    class FetchImage extends Thread{
        String URL1;
        String URL2;
        GameScoresAdapter.MyViewHolder holder;
        Bitmap bitmap1;
        Bitmap bitmap2;
        FetchImage(String URL1, String URL2, GameScoresAdapter.MyViewHolder holder){
            this.URL1 = URL1;
            this.URL2 = URL2;
            this.holder = holder;
        }

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Getting your games data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            InputStream inputStream = null;
            try {
                inputStream = new java.net.URL(URL1).openStream();
                bitmap1 = BitmapFactory.decodeStream(inputStream);
                inputStream = new java.net.URL(URL2).openStream();
                bitmap2 = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                        holder.teamImage1.setImageBitmap(bitmap1);
                        holder.teamImage2.setImageBitmap(bitmap2);
                    }
                }
            });

        }
    }*/
}
