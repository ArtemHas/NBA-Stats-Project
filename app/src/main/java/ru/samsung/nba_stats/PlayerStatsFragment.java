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

public class PlayerStatsFragment extends Fragment implements SelectListener {
    StorageReference storageReference;
    DatabaseReference reference;
    Handler handler;
    FirebaseDatabase db;
    RecyclerView recyclerView;
    ArrayList<Team> teamsArrayList = new ArrayList<>();

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
                        reference = db.getReference("urls");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String imageID = dataSnapshot.getKey();
                                    storageReference = FirebaseStorage.getInstance().getReference("team_logos/" + imageID + ".png");
                                    try {
                                        File localfile = File.createTempFile("tempfile", ".png");
                                        storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                                String[] arrOfStr = dataSnapshot.getKey().split("-");
                                                String teamNameFormatted = arrOfStr[0] + " " + arrOfStr[1];
                                                Team team = new Team(bitmap, teamNameFormatted, dataSnapshot.getValue().toString());
                                                teamsArrayList.add(team);
                                                teamListAdapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ld.dismissDialog();
                    }
                }, 3000);

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
        fragmentTransaction.hide(fm.findFragmentByTag("PlayerStatsFragmentTAG"));
        fragmentTransaction.add(R.id.frame_layout, new TeamRosterFragment(), "TeamRosterFragmentTAG");
        fragmentTransaction.commit();

    }
}