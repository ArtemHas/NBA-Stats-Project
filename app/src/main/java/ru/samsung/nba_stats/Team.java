package ru.samsung.nba_stats;

import android.graphics.Bitmap;

public class Team {
    Bitmap teamImage;
    String teamName, URL;
    public Team(Bitmap teamImage,String teamName, String URL) {
        this.teamImage = teamImage;
        this.teamName = teamName;
        this.URL = URL;
    }
    public Team(){}

    public Bitmap getTeamImage() {
        return teamImage;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getURL() {
        return URL;
    }

    public void setTeamImage(Bitmap teamImage) {
        this.teamImage = teamImage;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }



}
