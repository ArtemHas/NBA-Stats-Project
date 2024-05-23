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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class TeamRosterFragment extends Fragment implements SelectListenerRoster{
    final String TAG = "tag";
    private RecyclerView recyclerView;
    String URL;
    String teamName;
    ArrayList<PlayerInRosterForRecyclerView> playersArrayList = new ArrayList<>();
    List<PlayerInRoster> playersFromRoom = new ArrayList<>();

    public TeamRosterFragment() {

    }

    public static TeamRosterFragment newInstance() {
        TeamRosterFragment fragment = new TeamRosterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_roster, container, false);
        Button button = (Button) view.findViewById(R.id.buttonGoBack);
        TextView teamTextView = view.findViewById(R.id.teamNameRoster);

        getParentFragmentManager().setFragmentResultListener("dataFromPlayerPageFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                teamName = result.getString("teamName");
                URL = result.getString("URL");
                teamTextView.setText(teamName);
            }
        });
        getParentFragmentManager().setFragmentResultListener("dataFromPlayerStatsFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                teamName = result.getString("teamName");
                URL = result.getString("URL");
                teamTextView.setText(teamName);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new PlayerStatsFragment(), "PlayerStatsFragmentTAG");
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //LoadingDialog ld = new LoadingDialog(getActivity());
        //ld.startLoadingAnimation();

        ArrayList<String> playerName = new ArrayList<>();
        ArrayList<String> playerSalary = new ArrayList<>();
        ArrayList<Integer> playerExperience = new ArrayList<>();
        ArrayList<String> playerBirthDate = new ArrayList<>();
        ArrayList<String> playerJersey = new ArrayList<>();
        ArrayList<String> playerHeadshot = new ArrayList<>();
        ArrayList<String> playerURL = new ArrayList<>();
        ArrayList<String> playerPosition = new ArrayList<>();
        ArrayList<Integer> playerAge = new ArrayList<>();
        ArrayList<String> playerHeight = new ArrayList<>();

        recyclerView = getActivity().findViewById(R.id.teamRosterRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerListAdapter playerListAdapter = new PlayerListAdapter(getContext(), playersArrayList, this);
        recyclerView.setAdapter(playerListAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Thread getData = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                playersFromRoom = MyRoomPlayerInRosterDataBase.getInstance(getContext())
                                        .playerInRosterDao().loadAllByRosterURL(URL);
                                if (playersFromRoom.size() == 0) {
                                    Log.e(TAG, "getting from URL");
                                    List<PlayerInRoster> insertList = new ArrayList<>();
                                    try {
                                        java.net.URL urlObj = new URL(URL);
                                        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                                        connection.setRequestMethod("GET");
                                        int responseCode = connection.getResponseCode();

                                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                                            StringBuilder sb = new StringBuilder();
                                            Scanner scanner = new Scanner(connection.getInputStream());
                                            while (scanner.hasNext()) {
                                                sb.append(scanner.nextLine());
                                            }

                                            Pattern pattern = Pattern
                                                    .compile("\\{\"shortName\":\"[\\w\\s'.-]+\",\"name\":\"[\\w\\s'.-]+\",\"href\":\"[\\w:\\/._-]+\",\"uid\":\"[\\w~:]+\",\"guid\":\"[\\w-]+\",\"id\":\"\\w+\",\"height\":\"[\\w'\"\\s\\\\]+\",\"weight\":\"[\\w\\s]+\",\"age\":\\w+,\"position\":\"\\w+\",[\"hand\":\"\\w+\",]*[\"jersey\":\"\\w+\",]*[\"salary\":[\"]*[\\w$,]+[\"]*,]*\"birthDate\":\"[\\w\\/]+\",\"headshot\":\"[\\w:\\/\\.\\_\\-\\?\\=\\&]+\",\"lastName\":\"[\\w\\s'.-]+\",\"experience\":\\w+[,\"college\":\"[\\w\\s.'()&]+\"]*\\}");
                                            Matcher matcher = pattern.matcher(sb);
                                            ArrayList<JSONObject> games_json_obj = new ArrayList<JSONObject>();
                                            while (matcher.find()) {
                                                try {
                                                    games_json_obj.add(new JSONObject(sb.substring(matcher.start(), matcher.end()).concat("}")));
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                            for (JSONObject jsonObject : games_json_obj) {
                                                try {
                                                    playerName.add(jsonObject.getString("name"));
                                                    if(jsonObject.has("salary")){
                                                       playerSalary.add(String.valueOf(jsonObject.get("salary")));
                                                    }else{
                                                        playerSalary.add(null);
                                                    }
                                                    playerExperience.add(jsonObject.getInt("experience"));
                                                    playerBirthDate.add(jsonObject.getString("birthDate"));
                                                    if(jsonObject.has("jersey")){
                                                        playerJersey.add(jsonObject.getString("jersey"));
                                                    }else{
                                                        playerJersey.add(null);
                                                    }
                                                    playerHeadshot.add(jsonObject.getString("headshot"));
                                                    playerURL.add(jsonObject.getString("href"));
                                                    playerPosition.add(jsonObject.getString("position"));
                                                    playerAge.add(jsonObject.getInt("age"));
                                                    playerHeight.add(jsonObject.getString("height"));

                                                } catch (JSONException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                            for (int i = 0; i < playerName.size(); i++) {
                                                PlayerInRoster playerInRoster = new PlayerInRoster(
                                                        playerName.get(i),playerBirthDate.get(i),
                                                        playerAge.get(i),playerSalary.get(i),
                                                        playerExperience.get(i), playerJersey.get(i),
                                                        playerHeadshot.get(i),playerURL.get(i),
                                                        playerPosition.get(i),playerHeight.get(i),
                                                        URL
                                                );
                                                PlayerInRosterForRecyclerView playerInRosterForRecyclerView = new PlayerInRosterForRecyclerView(
                                                        playerName.get(i),playerBirthDate.get(i),
                                                        playerAge.get(i),playerSalary.get(i),
                                                        playerExperience.get(i), playerJersey.get(i),
                                                        playerHeadshot.get(i),playerURL.get(i),
                                                        playerPosition.get(i),playerHeight.get(i),
                                                        URL
                                                );
                                                insertList.add(playerInRoster);
                                                playersArrayList.add(playerInRosterForRecyclerView);
                                            }
                                        }
                                    } catch (Exception e) {}
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e(TAG, "ADDING TO ROOM");
                                            MyRoomPlayerInRosterDataBase.getInstance(getContext()).playerInRosterDao().insertMultiplePlayers(insertList);
                                        }
                                    }).start();
                                } else {
                                    Log.e(TAG, "getting from ROOM");
                                    for (PlayerInRoster playerInRoster : playersFromRoom) {
                                        PlayerInRosterForRecyclerView playerInRosterForRecyclerView= new PlayerInRosterForRecyclerView(
                                                playerInRoster.playerName, playerInRoster.playerBirthDate,
                                                playerInRoster.age, playerInRoster.playerSalary,
                                                playerInRoster.playerExperience, playerInRoster.playerJersey,
                                                playerInRoster.playerHeadshot, playerInRoster.playerURL,
                                                playerInRoster.playerPosition, playerInRoster.playerHeight,
                                                playerInRoster.rosterURL
                                        );
                                        playersArrayList.add(playerInRosterForRecyclerView);
                                    }
                                }
                            }

                        });
                        getData.start();
                        try {
                            getData.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        playerListAdapter.notifyDataSetChanged();
                        //ld.dismissDialog();
                    }

                });
            }
        }).start();
    }

    @Override
    public void onItemClicked(PlayerInRosterForRecyclerView playerInRosterForRecyclerView) {
        final FragmentManager fm = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Bundle result = new Bundle();
        result.putString("teamURL", URL);
        result.putString("teamName", teamName);
        result.putString("playerURL", playerInRosterForRecyclerView.playerURL);
        getParentFragmentManager().setFragmentResult("dataFromTeamRosterFragment", result);
        fragmentTransaction.replace(R.id.frame_layout, new PlayerPageFragment(), "PlayerPageFragmentTAG");
        fragmentTransaction.commit();
    }
}