package ru.samsung.nba_stats;

import android.graphics.Bitmap;

public class Game {
    public Game(Bitmap teamImage1, Bitmap teamImage2, String teamName1, String teamName2, Integer score1, Integer score2) {
        this.teamImage1 = teamImage1;
        this.teamImage2 = teamImage2;
        this.teamName1 = teamName1;
        this.teamName2 = teamName2;
        this.score1 = score1;
        this.score2 = score2;
    }

    Bitmap teamImage1,teamImage2;
    String teamName1,teamName2;
    Integer score1,score2;
}
