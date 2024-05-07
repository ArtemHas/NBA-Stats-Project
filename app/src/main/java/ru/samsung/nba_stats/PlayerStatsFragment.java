package ru.samsung.nba_stats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerStatsFragment extends Fragment {

    public PlayerStatsFragment() {

    }
    public static PlayerStatsFragment newInstance() {
        PlayerStatsFragment fragment = new PlayerStatsFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_stats, container, false);

        return view;
    }
}