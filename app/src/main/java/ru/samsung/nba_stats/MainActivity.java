package ru.samsung.nba_stats;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.samsung.nba_stats.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  implements GameScoresFragment.OnFragmentInteractionListener{
    private final String GameScoresFragmentTAG = "GameScoresFragmentTAG";
    private final String PlayerStatsFragmentTAG = "PlayerStatsFragmentTAG";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new GameScoresFragment(), this.GameScoresFragmentTAG);
        String game_scores_id = String.valueOf(R.id.game_scores);
        String player_stats_id = String.valueOf(R.id.player_stats);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(String.valueOf(item.getItemId()).equals(game_scores_id)){
                replaceFragment(new GameScoresFragment(), this.GameScoresFragmentTAG);
            }
            else if(String.valueOf(item.getItemId()).equals(player_stats_id)){
                replaceFragment(new PlayerStatsFragment(), this.PlayerStatsFragmentTAG);
            }


            return true;
        });
    }
    private void replaceFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}