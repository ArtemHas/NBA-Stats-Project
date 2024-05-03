package ru.samsung.nba_stats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameScoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameScoresFragment extends Fragment{
    EditText dateOfBirthET;
    Boolean noGames = false;
    String selectedDate;
    Handler mainHandler = new Handler();
    public static final int REQUEST_CODE = 11;
    ArrayList<Game> gamesArrayList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    OkHttpClient client;

    public GameScoresFragment() {

    }
    public static GameScoresFragment newInstance() {
        GameScoresFragment fragment = new GameScoresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = new OkHttpClient();
        /*Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = df.format(c);*/
        selectedDate = "13/04/2024";
        View view = inflater.inflate(R.layout.fragment_game_scores,container,false);
        Button btnShowDatePicker = (Button) view.findViewById(R.id.btnShowDatePicker);
        btnShowDatePicker.setText(selectedDate);
        final FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        btnShowDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(GameScoresFragment.this,REQUEST_CODE);
                newFragment.show(fm,"datePicker");

            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            selectedDate = data.getStringExtra("selectedDate");
            Log.e("data", selectedDate);
            Button button = (Button) getView().findViewById(R.id.btnShowDatePicker);
            button.setText(selectedDate);
            /*get();*/
            /*Runnable r = new Runnable() {
                @Override
                public void run(){
                    recyclerView = getView().findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setHasFixedSize(true);
                    GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
                    recyclerView.setAdapter(gameScoresAdapter);
                    gameScoresAdapter.notifyDataSetChanged();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, 2000);*/
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
        get.start();
        try {
            get.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(gamesArrayList.size() == 0) {
            noGames = true;
        }else{
            noGames = false;
        }
        if (noGames) {
            TextView noGamesMessage = view.findViewById(R.id.noGamesMessage);
            noGamesMessage.setText(R.string.no_games_message);
        } else {
            recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            Log.e("us", Integer.toString(gamesArrayList.size()));
            GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
            recyclerView.setAdapter(gameScoresAdapter);
            gameScoresAdapter.notifyDataSetChanged();
        }
    }
    Thread get = new Thread(){
        @Override
        public void run() {
            ArrayList<String> teamName1 = new ArrayList<String>();
            ArrayList<String> teamName2= new ArrayList<String>();
            ArrayList<Integer> score1= new ArrayList<Integer>();
            ArrayList<Integer> score2= new ArrayList<Integer>();
            ArrayList<Bitmap> teamImage1= new ArrayList<Bitmap>();
            ArrayList<Bitmap> teamImage2= new ArrayList<Bitmap>();

            String[] arrOfStr = selectedDate.split("/");
            String selectedDateFormatted = "";
            for (int i = arrOfStr.length-1; i > -1; i--) {
                selectedDateFormatted += arrOfStr[i];
            }
            String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + selectedDateFormatted;
            try {
                URL urlObj = new URL(newUrl);
                HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                System.out.println(responseCode);
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
                        games_json_obj.add(new JSONObject(sb.substring(matcher.start(), matcher.end()).concat("}")));
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
                            Bitmap bitmap1;
                            Bitmap bitmap2;
                            InputStream inputStream1 = null;
                            InputStream inputStream2 = null;
                            try{
                                inputStream1 = new java.net.URL(team_json_1.getString("logo")).openStream();
                                inputStream2 = new java.net.URL(team_json_2.getString("logo")).openStream();
                            }catch (Exception e){};
                            bitmap1 = BitmapFactory.decodeStream(inputStream1);
                            bitmap2 = BitmapFactory.decodeStream(inputStream2);
                            teamImage1.add(bitmap1);
                            teamImage2.add(bitmap2);
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

            }catch (Exception e){
                noGames = true;
            };
            for (int i = 0; i < teamName1.size(); i++){
                Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                gamesArrayList.add(game);

                Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " - " + score2.get(i) + " " + teamName2.get(i));
                Log.e("sys","----------------------------------------------------------------");
            }
        }
    };
}