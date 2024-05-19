package ru.samsung.nba_stats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerStatsFragment extends Fragment implements SelectListener {
    final String TAG = "tag";
    DatabaseReference reference;
    Handler handler;
    FirebaseDatabase db;
    RecyclerView recyclerView;
    ArrayList<Team> teamsArrayList = new ArrayList<>();
    List<Team> insertList = new ArrayList<>();

    public PlayerStatsFragment() {

    }

    public static PlayerStatsFragment newInstance() {

        PlayerStatsFragment fragment = new PlayerStatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance();
        View view = inflater.inflate(R.layout.fragment_player_stats, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        LoadingDialog ld = new LoadingDialog(getActivity());
        ld.startLoadingAnimation();

        recyclerView = view.findViewById(R.id.playerStatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TeamListAdapter teamListAdapter = new TeamListAdapter(getContext(), teamsArrayList, this);
        recyclerView.setAdapter(teamListAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Thread getData = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<Team> roomTeamsList = MyRoomTeamsDataBase.getInstance(getContext()).teamsDao().getAllTeams();
                                if (roomTeamsList.size() == 0){
                                    Log.e(TAG, "GETTING FROM FIREBASE");
                                    reference = db.getReference("urls");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String[] arrOfStr = dataSnapshot.getKey().split("-");
                                                String teamNameFormatted = arrOfStr[0] + " " + arrOfStr[1];
                                                String logo = "";
                                                String url = "";
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                    if(child.getKey().equals("logo")){
                                                        logo = String.valueOf(child.getValue());
                                                    }else{
                                                        url = String.valueOf(child.getValue());
                                                    }
                                                }
                                                Team team = new Team(logo, teamNameFormatted, url);
                                                teamsArrayList.add(team);
                                                insertList.add(team);
                                            }
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.e(TAG, "STORING");
                                                    MyRoomTeamsDataBase.getInstance(getContext()).teamsDao().insertMultipleTeams(insertList);
                                                    ld.dismissDialog();
                                                }
                                            }).start();
                                            teamListAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else{
                                    Log.e(TAG, "GETTING FROM ROOM");
                                    for (Team team : roomTeamsList) {
                                        teamsArrayList.add(team);
                                    }
                                    ld.dismissDialog();
                                }
                            }
                        });
                        getData.start();
                        try {
                            getData.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        teamListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(Team team) {

        final FragmentManager fm = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Bundle result = new Bundle();
        result.putString("URL", team.URL);
        result.putString("teamName", team.teamName);
        getParentFragmentManager().setFragmentResult("dataFromPlayerStatsFragment", result);
        fragmentTransaction.replace(R.id.frame_layout, new TeamRosterFragment(), "TeamRosterFragmentTAG");
        fragmentTransaction.commit();


    }
}

