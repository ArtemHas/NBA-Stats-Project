package ru.samsung.nba_stats;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GameR {
    @PrimaryKey (autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "team_name1")
    public String teamName1;
    @ColumnInfo (name = "team_name2")
    public String teamName2;
    @ColumnInfo (name = "score1")
    public int score1;
    @ColumnInfo (name = "score2")
    public int score2;
    @ColumnInfo(name = "teamImage1")
    public String teamImage1;
    @ColumnInfo(name = "teamImage2")
    public String teamImage2;
    @ColumnInfo(name = "isNone")
    public boolean isNone;
    public GameR(){

    }

    public GameR(String date, String teamName1, String teamName2, String teamImage1, String teamImage2, int score1, int score2, boolean isNone) {
        this.teamImage1 = teamImage1;
        this.teamImage2 =  teamImage2;
        this.isNone = isNone;
        this.date = date;
        this.teamName1 = teamName1;
        this.teamName2 = teamName2;
        this.score1 = score1;
        this.score2 = score2;
    }
}