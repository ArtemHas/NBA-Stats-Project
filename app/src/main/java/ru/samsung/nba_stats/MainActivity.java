package ru.samsung.nba_stats;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.samsung.nba_stats.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  implements GameScoresFragment.OnFragmentInteractionListener{
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        replaceFragment(new GameScoresFragment());

        String game_scores_id = String.valueOf(R.id.game_scores);
        String player_stats_id = String.valueOf(R.id.player_stats);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(String.valueOf(item.getItemId()).equals(game_scores_id)){
                replaceFragment(new GameScoresFragment());
            }
            else if(String.valueOf(item.getItemId()).equals(player_stats_id)){
                replaceFragment(new PlayerStatsFragment());
            }


            return true;
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}