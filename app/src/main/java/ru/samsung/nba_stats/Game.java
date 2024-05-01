package ru.samsung.nba_stats;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

public class Game {
    public Game(String teamImage1, String teamImage2, String teamName1, String teamName2, Integer score1, Integer score2) {
        this.teamImage1 = teamImage1;
        this.teamImage2 = teamImage2;
        this.teamName1 = teamName1;
        this.teamName2 = teamName2;
        this.score1 = score1;
        this.score2 = score2;
    }

    String teamImage1,teamImage2;
    String teamName1,teamName2;
    Integer score1,score2;
}
