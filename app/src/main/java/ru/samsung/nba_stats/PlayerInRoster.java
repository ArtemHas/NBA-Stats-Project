package ru.samsung.nba_stats;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlayerInRoster {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "playerName")
    String playerName;
    @ColumnInfo(name = "playerSalary")
    String playerSalary;
    @ColumnInfo(name = "playerExperience")
    int playerExperience;
    @ColumnInfo(name = "playerBirthDate")
    String playerBirthDate;
    @ColumnInfo(name = "playerJersey")
    String playerJersey;
    @ColumnInfo(name = "playerHeadshot")
    String playerHeadshot;
    @ColumnInfo(name = "playerURL")
    String playerURL;
    @ColumnInfo(name = "playerPosition")
    String playerPosition;
    @ColumnInfo(name = "playerAge")
    int age;
    @ColumnInfo(name = "playerHeight")
    String playerHeight;
    @ColumnInfo(name = "rosterURL")
    String rosterURL;
    public PlayerInRoster(){

    }

    public PlayerInRoster(String playerName,
                          String playerBirthDate,
                          int age,
                          String playerSalary,
                          int playerExperience,
                          String playerJersey,
                          String playerHeadshot,
                          String playerURL,
                          String playerPosition,
                          String playerHeight,
                          String URL) {
        this.rosterURL = URL;
        this.playerName = playerName;
        this.playerSalary = playerSalary;
        this.playerExperience = playerExperience;
        this.playerBirthDate = playerBirthDate;
        this.playerJersey = playerJersey;
        this.playerHeadshot = playerHeadshot;
        this.playerURL = playerURL;
        this.playerPosition = playerPosition;
        this.age = age;
        this.playerHeight = playerHeight;
    }
}
