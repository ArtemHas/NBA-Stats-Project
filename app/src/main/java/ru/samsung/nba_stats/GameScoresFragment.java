package ru.samsung.nba_stats;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = df.format(c);
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


        return view;
    }


   @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            selectedDate = data.getStringExtra("selectedDate");
            Button button = (Button) getView().findViewById(R.id.btnShowDatePicker);
            button.setText(selectedDate);
            gamesArrayList.clear();
            get();
            while (gamesArrayList.size() == 0 && noGames == false){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            TextView noGamesMessage = getActivity().findViewById(R.id.noGamesMessage);
            recyclerView = getActivity().findViewById(R.id.recyclerview);
            if (noGames){
                noGamesMessage.setText(R.string.no_games_message);
                gamesArrayList.clear();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                Log.e("us", Integer.toString(gamesArrayList.size()));
                GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
                recyclerView.setAdapter(gameScoresAdapter);
                Log.e("item count in adapter", String.valueOf(gameScoresAdapter.getItemCount()));
                gameScoresAdapter.notifyDataSetChanged();
                noGames = false;
            }else{
                noGamesMessage.setText("");
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                Log.e("us", Integer.toString(gamesArrayList.size()));
                GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
                recyclerView.setAdapter(gameScoresAdapter);
                Log.e("item count in adapter", String.valueOf(gameScoresAdapter.getItemCount()));
                gameScoresAdapter.notifyDataSetChanged();
            }
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
        get();
        /*try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }// while (gamesArrayList.size() == 0 && noGames == false)*/

        while (gamesArrayList.size() == 0 && noGames == false){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (noGames){
            TextView noGamesMessage = view.findViewById(R.id.noGamesMessage);
            noGamesMessage.setText(R.string.no_games_today_message);
            noGames = false;
        }else{
            recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.e("us", Integer.toString(gamesArrayList.size()));
            GameScoresAdapter gameScoresAdapter = new GameScoresAdapter(getContext(), gamesArrayList);
            recyclerView.setAdapter(gameScoresAdapter);
            gameScoresAdapter.notifyDataSetChanged();
            Log.e("item count in adapter", String.valueOf(gameScoresAdapter.getItemCount()));
        }
    }

    public void get(){
        String[] arrOfStr = selectedDate.split("/");
        String selectedDateFormatted = "";
        for (int i = arrOfStr.length-1; i > -1; i--) {
            selectedDateFormatted += arrOfStr[i];
        }
        String newUrl = "https://www.espn.com/nba/scoreboard/_/data/" + selectedDateFormatted;


        Request request = new Request.Builder().url(newUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                ArrayList<String> teamName1 = new ArrayList<String>();
                ArrayList<String> teamName2= new ArrayList<String>();
                ArrayList<Integer> score1= new ArrayList<Integer>();
                ArrayList<Integer> score2= new ArrayList<Integer>();
                ArrayList<Bitmap> teamImage1= new ArrayList<Bitmap>();
                ArrayList<Bitmap> teamImage2= new ArrayList<Bitmap>();

                String sb;
                try {
                    sb = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Pattern pattern = Pattern.compile("\\{\"id\":\"\\w+\",\"competitors\":\\[\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/_.\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Home\",\"type\":\"home\",\"summary\":\"[\\w\\-]+\"\\}]\\},\\{\"id\":\"\\w+\",\"abbrev\":\"\\w+\",\"displayName\":\"[\\w\\s]+\",\"shortDisplayName\":\"[\\w\\s-]+\",\"logo\":\"[\\w/\\-.:]+\",\"teamColor\":\"\\w+\",\"altColor\":\"\\w+\",\"uid\":\"[\\w/.:~]+\",\"recordSummary\":\"\\w*+\",\"standingSummary\":\"\\w*+\",\"location\":\"[\\w\\s]+\",\"links\":\"[\\w/\\-]+\",\"isHome\":\\w+,\"score\":\\w+,[\"winner\":true,]*\"records\":\\[\\{\"name\":\"overall\",\"abbreviation\":\"\\w+\",\"type\":\"\\w+\",\"summary\":\"[\\w\\-]+\"\\},\\{\"name\":\"Road\",\"type\":\"road\",\"summary\":\"[\\w\\-]+\"\\}]\\}],\"date\":\"[\\w\\-:]+\"");
                Matcher matcher = pattern.matcher(sb);
                ArrayList<JSONObject> games_json_obj = new ArrayList<JSONObject>();
                while (matcher.find()){
                    try {
                        games_json_obj.add(new JSONObject(sb.substring(matcher.start(), matcher.end()).concat("}")));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(games_json_obj.size() == 0){
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
                for (int i = 0; i < teamName1.size(); i++){
                    Game game = new Game(teamImage1.get(i), teamImage2.get(i), teamName1.get(i), teamName2.get(i), score1.get(i), score2.get(i));
                    gamesArrayList.add(game);
                    Log.e("sys", teamName1.get(i) + " " + score1.get(i) + " - " + score2.get(i) + " " + teamName2.get(i));
                    Log.e("sys","----------------------------------------------------------------");
                }

            }

        });

    }

}