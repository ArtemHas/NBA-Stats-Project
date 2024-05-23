package ru.samsung.nba_stats;

public class PlayerInRosterForRecyclerView {
    public String playerName;
    public String playerSalary;
    public int playerExperience;
    public String playerBirthDate;
    public String playerJersey;
    public String playerHeadshot;
    public String playerURL;
    public String playerPosition;
    public int age;
    public String playerHeight;
    public String rosterURL;
    public PlayerInRosterForRecyclerView(String playerName,
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
