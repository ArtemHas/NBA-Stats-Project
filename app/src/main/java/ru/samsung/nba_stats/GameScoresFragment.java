package ru.samsung.nba_stats;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;


public class GameScoresFragment extends Fragment {
    final String TAG = "tag";

    Boolean connectionFailed = false;
    Boolean noGames = false;
    String selectedDate = "09/04/2024";
    String todayDate;
    String todayDateFormatted = "";
    public static final int REQUEST_CODE = 11;
    List<GameR> gameRList = new ArrayList<>();
    ArrayList<Game> gamesArrayList = new ArrayList<>();
    ArrayList<String> loadedGamesSpinnerList = new ArrayList<>();

    String finalSelectedDateFormatted;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;


    public GameScoresFragment() {

    }

    public static GameScoresFragment newInstance() {
        return new GameScoresFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
        selectedDate = df.format(c);

        Log.e("selectedDate", selectedDate);
        todayDate = selectedDate;
        String[] arrOfStr = todayDate.split("/");
        for (int i = arrOfStr.length - 1; i > -1; i--) {
            todayDateFormatted += arrOfStr[i];
        }

        View view = inflater.inflate(R.layout.fragment_game_scores, container, false);
        Button btnShowDatePicker = (Button) view.findViewById(R.id.btnShowDatePicker);
        btnShowDatePicker.setText(selectedDate);
        final FragmentManager fm = getParentFragmentManager();
        btnShowDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(GameScoresFragment.this, REQUEST_CODE);
                newFragment.show(fm, "datePicker");
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            LoadingDialog ld = new LoadingDialog(getActivity());
            ld.startLoadingAnimation();
            Spinner spinner = getActivity().findViewById(R.id.loadedGamesSpinner);
            spinner.setSelection(0);
            selectedDate = data.getStringExtra("selectedDate");
            Button button = (Button) getView().findViewById(R.id.btnShowDatePicker);
            button.setText(selectedDate);
            noGames = false;
            gamesArrayList.clear();
            gameRList.clear();
            ArrayList<String> teamName1 = new ArrayList<>();
            ArrayList<String> teamName2 = new ArrayList<>();
            ArrayList<Integer> score1 = new ArrayList<>();
            ArrayList<Integer> score2 = new ArrayList<>();
            ArrayList<String> teamImage1 = new ArrayList<>();
            ArrayList<String> teamImage2 = new ArrayList<>();


            recyclerView = getActivity().findViewById(R.id.gameScoresRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
            recyclerView.setAdapter(gameScoresAdapter);
            TextView noGamesMessage = getActivity().findViewById(R.id.noGamesMessage);


            String[] arrOfStr = selectedDate.split("/");
            String selectedDateFormatted = "";
            for (int i = arrOfStr.length - 1; i > -1; i--) {
                selectedDateFormatted += arrOfStr[i];
            }

            finalSelectedDateFormatted = selectedDateFormatted;
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
                                    gameRList = MyRoomDataBase.getInstance(getContext()).gameDao().loadAllByDate(finalSelectedDateFormatted);
                                    if (gameRList.size() == 0) {
                                        Log.e(TAG, "getting from URL");
                                        List<GameR> insertList = new ArrayList<>();

                                        Log.e("sys", "getting from url");
                                        String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + finalSelectedDateFormatted;
                                        try {
                                            URL urlObj = new URL(newUrl);
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
                                                        .compile("\\{\"id\":\"\\w+\",\"competitors\":\\[\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/_.\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Home\",\"type\":\"home\",\"summary\":\"[\\w\\-]+\"\\}]\\},\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w/.:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Road\",\"type\":\"road\",\"summary\":\"[\\w\\-]+\"\\}]\\}],\"date\":\"[\\w\\-:]+\"");
                                                Matcher matcher = pattern.matcher(sb);
                                                ArrayList<JSONObject> games_json_obj = new ArrayList<JSONObject>();
                                                while (matcher.find()) {
                                                    try {
                                                        games_json_obj.add(new JSONObject(sb.substring(matcher.start(), matcher.end()).concat("}")));
                                                    } catch (JSONException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                if (games_json_obj.size() == 0) {
                                                    noGames = true;
                                                }
                                                for (JSONObject jsonObject : games_json_obj) {
                                                    try {
                                                        JSONArray competitors = jsonObject.getJSONArray("competitors");
                                                        JSONObject team_json_1 = new JSONObject(competitors.get(0).toString());
                                                        JSONObject team_json_2 = new JSONObject(competitors.get(1).toString());
                                                        teamName1.add(team_json_1.getString("displayName"));
                                                        teamName2.add(team_json_2.getString("displayName"));
                                                        score1.add(team_json_1.getInt("score"));
                                                        score2.add(team_json_2.getInt("score"));
                                                        teamImage1.add(team_json_1.getString("logo"));
                                                        teamImage2.add(team_json_2.getString("logo"));
                                                    } catch (JSONException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                }
                                                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                                                LocalDate selected = LocalDate.parse(selectedDate, f);
                                                LocalDate today = LocalDate.parse(todayDate, f);
                                                if (!noGames) {
                                                    for (int i = 0; i < teamName1.size(); i++) {
                                                        Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                                        GameR gameR = new GameR(finalSelectedDateFormatted, teamName1.get(i), teamName2.get(i), teamImage1.get(i)
                                                                , teamImage2.get(i), score1.get(i), score2.get(i), false);
                                                        gamesArrayList.add(game);
                                                        insertList.add(gameR);
                                                        noGamesMessage.setText("");
                                                    }
                                                    if (!selected.equals(today)) {
                                                        loadedGamesSpinnerList.add(selectedDate);
                                                        Log.e(TAG, "ADDING TO ROOM");
                                                        MyRoomDataBase.getInstance(getContext()).gameDao().insertMultipleGames(insertList);
                                                    }
                                                } else {
                                                    if (noGames && selected.isBefore(today)) {
                                                        noGamesMessage.setText(R.string.no_games_message);
                                                        GameR nullGame = new GameR(finalSelectedDateFormatted, null, null, null, null, 0, 0, true);
                                                        Log.e(TAG, "ADDING TO ROOM");
                                                        MyRoomDataBase.getInstance(getContext()).gameDao().insert(nullGame);
                                                        loadedGamesSpinnerList.add(selectedDate);
                                                    } else if (finalSelectedDateFormatted.equals(todayDateFormatted) && noGames) {
                                                        Log.e(TAG, "THIS IS TODAY DATE");
                                                        noGamesMessage.setText(R.string.no_games_today_yet_message);
                                                    } else if (selected.isAfter(today)) {
                                                        Log.e(TAG, "THIS IS THE FUTURE DATE");
                                                        noGamesMessage.setText(R.string.incorrect_date_message);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        Log.e(TAG, "getting from ROOM");
                                        for (GameR gameR : gameRList) {
                                            if (gameR.isNone) {
                                                noGamesMessage.setText(R.string.no_games_message);
                                                Log.e(TAG, "ROOM SAYS THERE ARE NO GAMES");
                                                break;
                                            }
                                            noGamesMessage.setText("");
                                            Game game = new Game(gameR.teamImage1, gameR.teamImage2, gameR.teamName1, gameR.teamName2, gameR.score1, gameR.score2);
                                            gamesArrayList.add(game);
                                        }
                                    }
                                    //gameScoresAdapter.notifyDataSetChanged();
                                }

                            });
                            getData.start();
                            try {
                                getData.join();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            gameScoresAdapter.notifyDataSetChanged();
                            ld.dismissDialog();


                        }

                    });
                }
            }).start();
        }

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionFailed = false;
        LoadingDialog ld = new LoadingDialog(getActivity());
        ld.startLoadingAnimation();
        recyclerView = view.findViewById(R.id.gameScoresRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
        recyclerView.setAdapter(gameScoresAdapter);
        TextView noGamesMessage = view.findViewById(R.id.noGamesMessage);
        Spinner spinner = view.findViewById(R.id.loadedGamesSpinner);
        Button button = (Button) getView().findViewById(R.id.btnShowDatePicker);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "I HAVE RETURNED TO LISTENER");
                GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
                recyclerView.setAdapter(gameScoresAdapter);
                String item = parent.getItemAtPosition(position).toString();
                if (!(position == 0)) {
                    gameRList.clear();
                    gamesArrayList.clear();
                    Log.e(TAG, "SELECTED FROM LOADED DATES: " + item);
                    button.setText(item);


                    String[] arrOfStr = item.split("/");
                    String selectedDateFormatted = "";
                    for (int i = arrOfStr.length - 1; i > -1; i--) {
                        selectedDateFormatted += arrOfStr[i];
                    }

                    finalSelectedDateFormatted = selectedDateFormatted;
                    spinner.setSelection(0);

                    Thread getGamesFromSpinner = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gameRList = MyRoomDataBase.getInstance(getContext()).gameDao().loadAllByDate(finalSelectedDateFormatted);
                            Log.e(TAG, "getting from ROOM");
                            for (GameR gameR : gameRList) {
                                if (gameR.isNone) {
                                    noGamesMessage.setText(R.string.no_games_message);
                                    Log.e(TAG, "ROOM SAYS THERE ARE NO GAMES");
                                    break;
                                }
                                noGamesMessage.setText("");
                                Game game = new Game(gameR.teamImage1, gameR.teamImage2, gameR.teamName1, gameR.teamName2, gameR.score1, gameR.score2);
                                gamesArrayList.add(game);
                            }
                        }
                    });
                    getGamesFromSpinner.start();
                    try {
                        getGamesFromSpinner.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    gameScoresAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadedGamesSpinnerList.add(0, "Select from loaded dates");
        Thread loadedGames = new Thread(new Runnable() {
            @Override
            public void run() {
                List<GameR> loadedGames = MyRoomDataBase.getInstance(getContext()).gameDao().getAll();
                ArrayList<String> t = new ArrayList<>();
                for (GameR loadedGame : loadedGames) {
                    t.add(loadedGame.date);
                }
                Set<String> loadedGamesDates = new HashSet<>(t);
                for (String date : loadedGamesDates) {
                    String year = date.substring(0, 4);
                    String month = date.substring(4, 6);
                    String day = date.substring(6, 8);
                    loadedGamesSpinnerList.add(day + "/" + month + "/" + year);
                }
            }
        });
        loadedGames.start();
        try {
            loadedGames.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Log.e(TAG, String.valueOf(loadedGamesSpinnerList));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, loadedGamesSpinnerList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(spinnerAdapter);


        ArrayList<String> teamName1 = new ArrayList<>();
        ArrayList<String> teamName2 = new ArrayList<>();
        ArrayList<Integer> score1 = new ArrayList<>();
        ArrayList<Integer> score2 = new ArrayList<>();
        ArrayList<String> teamImage1 = new ArrayList<>();
        ArrayList<String> teamImage2 = new ArrayList<>();


        String[] arrOfStr = selectedDate.split("/");
        String selectedDateFormatted = "";
        for (int i = arrOfStr.length - 1; i > -1; i--) {
            selectedDateFormatted += arrOfStr[i];
        }

        finalSelectedDateFormatted = selectedDateFormatted;
        new Thread(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Thread get_data = new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {

                                MyRoomPlayerInRosterDataBase.getInstance(getContext()).playerInRosterDao().nukeTable();

                                String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + finalSelectedDateFormatted;
                                try {
                                    URL urlObj = new URL(newUrl);
                                    HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                                    connection.setRequestMethod("GET");
                                    int responseCode = connection.getResponseCode();

                                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                                        StringBuilder sb = new StringBuilder();
                                        Scanner scanner = new Scanner(connection.getInputStream());
                                        while (scanner.hasNext()) {
                                            sb.append(scanner.nextLine());
                                        }
                                        Pattern pattern = Pattern.compile("\\{\"id\":\"\\w+\",\"competitors\":\\[\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/_.\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Home\",\"type\":\"home\",\"summary\":\"[\\w\\-]+\"\\}]\\},\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w/.:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Road\",\"type\":\"road\",\"summary\":\"[\\w\\-]+\"\\}]\\}],\"date\":\"[\\w\\-:]+\"");
                                        Matcher matcher = pattern.matcher(sb);
                                        ArrayList<JSONObject> games_json_obj = new ArrayList<JSONObject>();
                                        while (matcher.find()) {
                                            try {
                                                games_json_obj.add(new JSONObject(sb.substring(matcher.start(), matcher.end()).concat("}")));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        if (games_json_obj.size() == 0) {
                                            noGames = true;
                                        }
                                        for (JSONObject jsonObject : games_json_obj) {
                                            try {
                                                JSONArray competitors = jsonObject.getJSONArray("competitors");
                                                JSONObject team_json_1 = new JSONObject(competitors.get(0).toString());
                                                JSONObject team_json_2 = new JSONObject(competitors.get(1).toString());
                                                teamName1.add(team_json_1.getString("displayName"));
                                                teamName2.add(team_json_2.getString("displayName"));
                                                score1.add(team_json_1.getInt("score"));
                                                score2.add(team_json_2.getInt("score"));
                                                teamImage1.add(team_json_1.getString("logo"));
                                                teamImage2.add(team_json_2.getString("logo"));

                                                //Log.e("sys" , String.valueOf(jsonObject.getString("date")));
                                                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");
                                                LocalDateTime time = LocalDateTime.parse(String.valueOf(jsonObject.getString("date")), f);
                                                LocalDateTime approximateEndTime = time.plusHours(2).plusMinutes(45);

                                                LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(0));
                                                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                                                String strCurrentTime = fmt.format(now);
                                                LocalDateTime currentTimeFormatted = LocalDateTime.parse(strCurrentTime, fmt);
                                                //Log.e("currentTime", String.valueOf(currentTimeFormatted));
                                                //Log.e("isAfter", String.valueOf(currentTimeFormatted.isAfter(approximateEndTime)));
                                            } catch (JSONException ex) {
                                                throw new RuntimeException(ex);
                                            }

                                        }
                                        for (int i = 0; i < teamName1.size(); i++) {
                                            Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                            gamesArrayList.add(game);
                                        }
                                        ld.dismissDialog();
                                    }else{
                                    }
                                } catch (Exception e) {
                                    connectionFailed = true;
                                    ld.dismissDialog();
                                    Log.e(TAG, "connection failed");
                                }
                            }
                        });
                        get_data.start();
                        try {
                            get_data.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        gameScoresAdapter.notifyDataSetChanged();
                        if (noGames) {
                            noGamesMessage.setText(R.string.no_games_today_yet_message);
                            noGames = false;
                        } else {
                            noGamesMessage.setText("");
                        }

                        if(connectionFailed){
                            noGamesMessage.setText(R.string.internet_problems);
                        }
                    }

                });
            }
        }).start();

    }

}