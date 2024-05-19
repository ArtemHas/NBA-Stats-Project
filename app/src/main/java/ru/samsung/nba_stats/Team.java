package ru.samsung.nba_stats;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Team {
    @PrimaryKey(autoGenerate = true)
     public int uid;
    @ColumnInfo(name = "team_image")
    String teamImage;
    @ColumnInfo(name = "team_name")
    String teamName;
    @ColumnInfo (name = "url")
    String URL;
    public Team(){

    }
    public Team(String teamImage,String teamName, String URL) {
        this.teamImage = teamImage;
        this.teamName = teamName;
        this.URL = URL;
    }




}
