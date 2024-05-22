package ru.samsung.nba_stats;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import java.io.DataInputStream;
import java.net.URL;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class PlayerPageFragment extends Fragment {
    final String TAG = "tag";
    String teamName;
    String playerURL;
    String teamURL;
    PlayerInRoster player;

    public PlayerPageFragment() {

    }

    public static PlayerPageFragment newInstance() {
        PlayerPageFragment fragment = new PlayerPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_page, container, false);
        Button button = (Button) view.findViewById(R.id.buttonGoBackPlayerPage);

        getParentFragmentManager().setFragmentResultListener("dataFromTeamRosterFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                teamName = result.getString("teamName");
                playerURL = result.getString("playerURL");
                teamURL = result.getString("teamURL");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Bundle result = new Bundle();
                result.putString("URL", teamURL);
                result.putString("teamName", teamName);
                getParentFragmentManager().setFragmentResult("dataFromPlayerPageFragment", result);
                fragmentTransaction.replace(R.id.frame_layout, new TeamRosterFragment(), "TeamRosterFragmentTAG");
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}

