package ru.samsung.nba_stats;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;

import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GameScoresFragment extends Fragment {

    Boolean noGames = false;
    String selectedDate;
    String todayDate;
    String todayDateFormatted = "";
    Handler mainHandler = new Handler();
    public static final int REQUEST_CODE = 11;
    ArrayList<Game> gamesArrayList = new ArrayList<>();
    ArrayList<GameForDB> gamesForDBArrayList = new ArrayList<>();

    FirebaseDatabase db;
    String finalSelectedDateFormatted;
    DatabaseReference reference;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    OkHttpClient client;

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
        db = FirebaseDatabase.getInstance();
        client = new OkHttpClient();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT-04:00"));
        selectedDate = df.format(c);
        todayDate = selectedDate;
        Log.e("sys", "---------------" + todayDate);
        String[] arrOfStr = todayDate.split("/");
        for (int i = arrOfStr.length - 1; i > -1; i--) {
            todayDateFormatted += arrOfStr[i];
        }


        View view = inflater.inflate(R.layout.fragment_game_scores, container, false);
        Button btnShowDatePicker = (Button) view.findViewById(R.id.btnShowDatePicker);
        btnShowDatePicker.setText(selectedDate);
        final FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
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
            selectedDate = data.getStringExtra("selectedDate");
            Button button = (Button) getView().findViewById(R.id.btnShowDatePicker);
            button.setText(selectedDate);
            gamesArrayList.clear();

            ArrayList<String> teamName1 = new ArrayList<String>();
            ArrayList<String> teamName2 = new ArrayList<String>();
            ArrayList<Integer> score1 = new ArrayList<Integer>();
            ArrayList<Integer> score2 = new ArrayList<Integer>();
            ArrayList<Bitmap> teamImage1 = new ArrayList<Bitmap>();
            ArrayList<Bitmap> teamImage2 = new ArrayList<Bitmap>();
            ArrayList<String> teamLogo1 = new ArrayList<>();
            ArrayList<String> teamLogo2 = new ArrayList<>();

            recyclerView = getActivity().findViewById(R.id.recyclerview);
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
            reference = db.getReference("game_days");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            reference.orderByKey().equalTo(finalSelectedDateFormatted).addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.e("sys", String.valueOf(snapshot.exists()));
                                    if (snapshot.exists()) {
                                        reference = db.getReference("game_days/" + finalSelectedDateFormatted);
                                        Log.e("getting from db", "getting from db");
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Log.e("sys", snapshot.getValue().toString());
                                                if (snapshot.getValue().toString().equals("none")) {
                                                    noGames = true;
                                                    Log.e("sys", "zaebis");
                                                } else {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        GameForDB gameForDB = dataSnapshot.getValue(GameForDB.class);
                                                        teamName1.add(gameForDB.teamName1);
                                                        teamName2.add(gameForDB.teamName2);
                                                        score1.add(gameForDB.score1);
                                                        score2.add(gameForDB.score2);

                                                        Thread fetch = new Thread(new Runnable() {
                                                            public void run() {
                                                                try {
                                                                    Bitmap bitmap1;
                                                                    Bitmap bitmap2;
                                                                    InputStream inputStream1 = null;
                                                                    InputStream inputStream2 = null;
                                                                    inputStream1 = new URL(gameForDB.teamImage1).openStream();
                                                                    inputStream2 = new URL(gameForDB.teamImage2).openStream();
                                                                    bitmap1 = BitmapFactory.decodeStream(inputStream1);
                                                                    bitmap2 = BitmapFactory.decodeStream(inputStream2);
                                                                    Log.e("bitmap", String.valueOf(bitmap1));
                                                                    Log.e("bitmap", String.valueOf(bitmap2));
                                                                    teamImage1.add(bitmap1);
                                                                    teamImage2.add(bitmap2);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                        fetch.start();
                                                        try {
                                                            fetch.join();
                                                        } catch (InterruptedException e) {
                                                            throw new RuntimeException(e);
                                                        }

                                                    }
                                                    for (int i = 0; i < teamName1.size(); i++) {
                                                        Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                                        Log.e("sys", game.teamName1 + " " + game.score1 + " " + game.score2 + " " + game.teamName2);
                                                        Log.e("this", String.valueOf(teamImage1.get(i)));
                                                        gamesArrayList.add(game);
                                                    }
                                                }
                                                gameScoresAdapter.notifyDataSetChanged();
                                                if (noGames) {
                                                    noGamesMessage.setText(R.string.no_games_message);
                                                    noGames = false;
                                                } else {
                                                    noGamesMessage.setText("");
                                                }
                                                ld.dismissDialog();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    } else {
                                        Thread get_data = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
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
                                                                teamLogo1.add(team_json_1.getString("logo"));
                                                                teamLogo2.add(team_json_2.getString("logo"));
                                                                Bitmap bitmap1;
                                                                Bitmap bitmap2;
                                                                InputStream inputStream1 = null;
                                                                InputStream inputStream2 = null;
                                                                try {
                                                                    inputStream1 = new URL(team_json_1.getString("logo")).openStream();
                                                                    inputStream2 = new URL(team_json_2.getString("logo")).openStream();
                                                                } catch (Exception e) {
                                                                }

                                                                bitmap1 = BitmapFactory.decodeStream(inputStream1);
                                                                bitmap2 = BitmapFactory.decodeStream(inputStream2);
                                                                teamImage1.add(bitmap1);
                                                                teamImage2.add(bitmap2);
                                                            } catch (JSONException ex) {
                                                                throw new RuntimeException(ex);
                                                            }
                                                        }
                                                        for (int i = 0; i < teamName1.size(); i++) {
                                                            Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                                            GameForDB gameForDB = new GameForDB(teamLogo1.get(i), teamLogo2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                                            Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " " + score2.get(i) + " " + teamName2.get(i));
                                                            gamesArrayList.add(game);
                                                            gamesForDBArrayList.add(gameForDB);
                                                        }

                                                    }
                                                } catch (Exception e) {
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
                                        reference = db.getReference("game_days");

                                        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                                        LocalDate selected = LocalDate.parse(selectedDate, f);
                                        LocalDate today = LocalDate.parse(todayDate, f);
                                        Log.e("dat", finalSelectedDateFormatted);
                                        Log.e("dat", todayDateFormatted);
                                        Log.e("dat", String.valueOf(selected.isBefore(today)));
                                        Log.e("No games", String.valueOf(noGames));
                                        if (noGames && selected.isBefore(today)) {
                                            Log.e("from no games", "from no games");
                                            noGamesMessage.setText(R.string.no_games_message);
                                            reference.child(finalSelectedDateFormatted).setValue("none").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.e("sys", "written succesfully");
                                                }
                                            });
                                            noGames = false;
                                        } else if (selected.isBefore(today) && !noGames) {
                                            noGamesMessage.setText("");
                                            for (int i = 0; i < gamesForDBArrayList.size(); i++) {
                                                reference.child(finalSelectedDateFormatted).child(String.valueOf(i)).setValue(gamesForDBArrayList.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.e("sys", "written succesfully");
                                                    }
                                                });
                                            }
                                        } else if (finalSelectedDateFormatted.equals(todayDateFormatted) && noGames) {
                                            noGamesMessage.setText(R.string.no_games_today_yet_message);
                                            noGames = false;
                                        } else if (selected.isAfter(today)) {
                                            noGamesMessage.setText(R.string.incorrect_date_message);
                                        }
                                        ld.dismissDialog();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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
            throw new RuntimeException(context.toString()
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
        LoadingDialog ld = new LoadingDialog(getActivity());
        ld.startLoadingAnimation();
        ArrayList<String> teamName1 = new ArrayList<String>();
        ArrayList<String> teamName2 = new ArrayList<String>();
        ArrayList<Integer> score1 = new ArrayList<Integer>();
        ArrayList<Integer> score2 = new ArrayList<Integer>();
        ArrayList<Bitmap> teamImage1 = new ArrayList<Bitmap>();
        ArrayList<Bitmap> teamImage2 = new ArrayList<Bitmap>();
        ArrayList<String> teamLogo1 = new ArrayList<>();
        ArrayList<String> teamLogo2 = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
        recyclerView.setAdapter(gameScoresAdapter);
        TextView noGamesMessage = view.findViewById(R.id.noGamesMessage);


        String[] arrOfStr = selectedDate.split("/");
        String selectedDateFormatted = "";
        for (int i = arrOfStr.length - 1; i > -1; i--) {
            selectedDateFormatted += arrOfStr[i];
        }

        finalSelectedDateFormatted = selectedDateFormatted;
        reference = db.getReference("game_days");
        new Thread(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Thread get_data = new Thread(new Runnable() {
                            @Override
                            public void run() {
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
                                                teamLogo1.add(team_json_1.getString("logo"));
                                                teamLogo2.add(team_json_2.getString("logo"));
                                                Bitmap bitmap1;
                                                Bitmap bitmap2;
                                                InputStream inputStream1 = null;
                                                InputStream inputStream2 = null;
                                                try {
                                                    inputStream1 = new URL(team_json_1.getString("logo")).openStream();
                                                    inputStream2 = new URL(team_json_2.getString("logo")).openStream();
                                                } catch (Exception e) {
                                                }

                                                bitmap1 = BitmapFactory.decodeStream(inputStream1);
                                                bitmap2 = BitmapFactory.decodeStream(inputStream2);
                                                teamImage1.add(bitmap1);
                                                teamImage2.add(bitmap2);
                                            } catch (JSONException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        }
                                        for (int i = 0; i < teamName1.size(); i++) {
                                            Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                            GameForDB gameForDB = new GameForDB(teamLogo1.get(i), teamLogo2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                            Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " " + score2.get(i) + " " + teamName2.get(i));
                                            gamesArrayList.add(game);
                                            gamesForDBArrayList.add(gameForDB);
                                        }

                                    }
                                } catch (Exception e) {
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
                        ld.dismissDialog();

                    }

                });
            }
        }).start();


        /*try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }// while (gamesArrayList.size() == 0 && noGames == false)*/


        /*if (noGames){
            TextView noGamesMessage = view.findViewById(R.id.noGamesMessage);
            noGamesMessage.setText(R.string.no_games_today_message);
            noGames = false;
        }else{

            gameScoresAdapter.notifyDataSetChanged();
            Log.e("item count in adapter", String.valueOf(gameScoresAdapter.getItemCount()));
        }*/
    }

    public void getFromURL(boolean isToday) {
        ArrayList<String> teamName1 = new ArrayList<String>();
        ArrayList<String> teamName2 = new ArrayList<String>();
        ArrayList<Integer> score1 = new ArrayList<Integer>();
        ArrayList<Integer> score2 = new ArrayList<Integer>();
        ArrayList<Bitmap> teamImage1 = new ArrayList<Bitmap>();
        ArrayList<Bitmap> teamImage2 = new ArrayList<Bitmap>();
        ArrayList<String> teamLogo1 = new ArrayList<>();
        ArrayList<String> teamLogo2 = new ArrayList<>();

        String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + finalSelectedDateFormatted;

        Request request = new Request.Builder().url(newUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String sb;
                try {
                    sb = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
                    Log.e("NOGAMES", String.valueOf(noGames));
                    reference.child(finalSelectedDateFormatted).setValue("none").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("sys", "written successfully");
                        }
                    });
                } else {
                    for (JSONObject jsonObject : games_json_obj) {
                        try {
                            JSONArray competitors = jsonObject.getJSONArray("competitors");
                            JSONObject team_json_1 = new JSONObject(competitors.get(0).toString());
                            JSONObject team_json_2 = new JSONObject(competitors.get(1).toString());
                            teamName1.add(team_json_1.getString("displayName"));
                            teamName2.add(team_json_2.getString("displayName"));
                            score1.add(team_json_1.getInt("score"));
                            score2.add(team_json_2.getInt("score"));
                            teamLogo1.add(team_json_1.getString("logo"));
                            teamLogo2.add(team_json_2.getString("logo"));
                            Bitmap bitmap1;
                            Bitmap bitmap2;
                            InputStream inputStream1 = null;
                            InputStream inputStream2 = null;
                            try {
                                inputStream1 = new java.net.URL(team_json_1.getString("logo")).openStream();
                                inputStream2 = new java.net.URL(team_json_2.getString("logo")).openStream();
                            } catch (Exception e) {
                            }

                            bitmap1 = BitmapFactory.decodeStream(inputStream1);
                            bitmap2 = BitmapFactory.decodeStream(inputStream2);
                            teamImage1.add(bitmap1);
                            teamImage2.add(bitmap2);
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    if (isToday) {
                        for (int i = 0; i < teamName1.size(); i++) {
                            Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                            Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " " + score2.get(i) + " " + teamName2.get(i));
                            gamesArrayList.add(game);
                        }
                    } else {
                        for (int i = 0; i < teamName1.size(); i++) {
                            Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                            GameForDB gameForDB = new GameForDB(teamLogo1.get(i), teamLogo2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                            Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " " + score2.get(i) + " " + teamName2.get(i));
                            gamesArrayList.add(game);
                            gamesForDBArrayList.add(gameForDB);
                        }
                        reference = db.getReference("game_days");
                        for (int i = 0; i < gamesForDBArrayList.size(); i++) {
                            reference.child(finalSelectedDateFormatted).child(String.valueOf(i)).setValue(gamesForDBArrayList.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("sys", "written succesfully");
                                }
                            });
                        }
                    }

                }
            }

        });
    }


    public void getFromDB() {

        ArrayList<String> teamName1 = new ArrayList<String>();
        ArrayList<String> teamName2 = new ArrayList<String>();
        ArrayList<Integer> score1 = new ArrayList<Integer>();
        ArrayList<Integer> score2 = new ArrayList<Integer>();
        ArrayList<Bitmap> teamImage1 = new ArrayList<Bitmap>();
        ArrayList<Bitmap> teamImage2 = new ArrayList<Bitmap>();
        ArrayList<String> teamLogo1 = new ArrayList<>();
        ArrayList<String> teamLogo2 = new ArrayList<>();

        reference = db.getReference("game_days/" + finalSelectedDateFormatted);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("sys", snapshot.getValue().toString());
                if (snapshot.getValue().toString().equals("none")) {
                    noGames = true;
                    Log.e("sys", "zaebis");
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GameForDB gameForDB = dataSnapshot.getValue(GameForDB.class);
                        teamName1.add(gameForDB.teamName1);
                        teamName2.add(gameForDB.teamName2);
                        score1.add(gameForDB.score1);
                        score2.add(gameForDB.score2);
                        Bitmap bitmap1;
                        Bitmap bitmap2;
                        InputStream inputStream1 = null;
                        InputStream inputStream2 = null;
                        try {
                            inputStream1 = new java.net.URL(gameForDB.teamImage1).openStream();
                            inputStream2 = new java.net.URL(gameForDB.teamImage2).openStream();
                        } catch (Exception e) {
                        }
                        bitmap1 = BitmapFactory.decodeStream(inputStream1);
                        bitmap2 = BitmapFactory.decodeStream(inputStream2);
                        teamImage1.add(bitmap1);
                        teamImage2.add(bitmap2);
                    }
                    for (int i = 0; i < teamName1.size(); i++) {
                        Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                        Log.e("sys", game.teamName1 + " " + game.score1 + " " + game.score2 + " " + game.teamName2);
                        gamesArrayList.add(game);
                    }
                }
                Log.e("worked with db", "worked with db");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void get() {

        ArrayList<String> teamName1 = new ArrayList<String>();
        ArrayList<String> teamName2 = new ArrayList<String>();
        ArrayList<Integer> score1 = new ArrayList<Integer>();
        ArrayList<Integer> score2 = new ArrayList<Integer>();
        ArrayList<Bitmap> teamImage1 = new ArrayList<Bitmap>();
        ArrayList<Bitmap> teamImage2 = new ArrayList<Bitmap>();
        ArrayList<String> teamLogo1 = new ArrayList<>();
        ArrayList<String> teamLogo2 = new ArrayList<>();

        String[] arrOfStr = selectedDate.split("/");
        String selectedDateFormatted = "";
        for (int i = arrOfStr.length - 1; i > -1; i--) {
            selectedDateFormatted += arrOfStr[i];
        }
        String finalSelectedDateFormatted = selectedDateFormatted;

        reference = db.getReference("game_days");

        reference.orderByKey().equalTo(finalSelectedDateFormatted).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("sys", String.valueOf(snapshot.exists()));
                if (snapshot.exists()) {
                    reference = db.getReference("game_days/" + finalSelectedDateFormatted);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.e("sys", snapshot.getValue().toString());
                            if (snapshot.getValue().toString().equals("none")) {
                                noGames = true;
                                Log.e("sys", "zaebis");
                            } else {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    GameForDB gameForDB = dataSnapshot.getValue(GameForDB.class);
                                    teamName1.add(gameForDB.teamName1);
                                    teamName2.add(gameForDB.teamName2);
                                    score1.add(gameForDB.score1);
                                    score2.add(gameForDB.score2);
                                    Bitmap bitmap1;
                                    Bitmap bitmap2;
                                    InputStream inputStream1 = null;
                                    InputStream inputStream2 = null;
                                    try {
                                        inputStream1 = new java.net.URL(gameForDB.teamImage1).openStream();
                                        inputStream2 = new java.net.URL(gameForDB.teamImage2).openStream();
                                    } catch (Exception e) {
                                    }
                                    bitmap1 = BitmapFactory.decodeStream(inputStream1);
                                    bitmap2 = BitmapFactory.decodeStream(inputStream2);
                                    teamImage1.add(bitmap1);
                                    teamImage2.add(bitmap2);
                                }
                                for (int i = 0; i < teamName1.size(); i++) {
                                    Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                    Log.e("sys", game.teamName1 + " " + game.score1 + " " + game.score2 + " " + game.teamName2);
                                    gamesArrayList.add(game);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + finalSelectedDateFormatted;

                    Request request = new Request.Builder().url(newUrl).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            e.printStackTrace();

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            String sb;
                            try {
                                sb = response.body().string();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
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
                                Log.e("NOGAMES", String.valueOf(noGames));
                                reference.child(finalSelectedDateFormatted).setValue("none").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e("sys", "written succesfully");
                                    }
                                });
                            } else {
                                for (JSONObject jsonObject : games_json_obj) {
                                    try {
                                        JSONArray competitors = jsonObject.getJSONArray("competitors");
                                        JSONObject team_json_1 = new JSONObject(competitors.get(0).toString());
                                        JSONObject team_json_2 = new JSONObject(competitors.get(1).toString());
                                        teamName1.add(team_json_1.getString("displayName"));
                                        teamName2.add(team_json_2.getString("displayName"));
                                        score1.add(team_json_1.getInt("score"));
                                        score2.add(team_json_2.getInt("score"));
                                        teamLogo1.add(team_json_1.getString("logo"));
                                        teamLogo2.add(team_json_2.getString("logo"));
                                        Bitmap bitmap1;
                                        Bitmap bitmap2;
                                        InputStream inputStream1 = null;
                                        InputStream inputStream2 = null;
                                        try {
                                            inputStream1 = new java.net.URL(team_json_1.getString("logo")).openStream();
                                            inputStream2 = new java.net.URL(team_json_2.getString("logo")).openStream();
                                        } catch (Exception e) {
                                        }

                                        bitmap1 = BitmapFactory.decodeStream(inputStream1);
                                        bitmap2 = BitmapFactory.decodeStream(inputStream2);
                                        teamImage1.add(bitmap1);
                                        teamImage2.add(bitmap2);
                                    } catch (JSONException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                for (int i = 0; i < teamName1.size(); i++) {
                                    Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                    GameForDB gameForDB = new GameForDB(teamLogo1.get(i), teamLogo2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                                    Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " " + score2.get(i) + " " + teamName2.get(i));
                                    gamesArrayList.add(game);
                                    gamesForDBArrayList.add(gameForDB);
                                }
                                reference = db.getReference("game_days");
                                for (int i = 0; i < gamesForDBArrayList.size(); i++) {
                                    reference.child(finalSelectedDateFormatted).child(String.valueOf(i)).setValue(gamesForDBArrayList.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.e("sys", "written succesfully");
                                        }
                                    });
                                }
                            }
                        }

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}