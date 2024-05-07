package ru.samsung.nba_stats;


public class GameForDB {
    public GameForDB(String teamImage1, String teamImage2, String teamName1, String teamName2, Integer score1, Integer score2) {
        this.teamImage1 = teamImage1;
        this.teamImage2 = teamImage2;
        this.teamName1 = teamName1;
        this.teamName2 = teamName2;
        this.score1 = score1;
        this.score2 = score2;
    }

    public GameForDB() {
    }

    String teamImage1,teamImage2;
    String teamName1,teamName2;
    Integer score1,score2;

    public void setTeamImage1(String teamImage1) {
        this.teamImage1 = teamImage1;
    }

    public void setTeamImage2(String teamImage2) {
        this.teamImage2 = teamImage2;
    }

    public void setTeamName1(String teamName1) {
        this.teamName1 = teamName1;
    }

    public void setTeamName2(String teamName2) {
        this.teamName2 = teamName2;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
    }

    public String getTeamImage1() {
        return teamImage1;
    }

    public String getTeamImage2() {
        return teamImage2;
    }

    public String getTeamName1() {
        return teamName1;
    }

    public String getTeamName2() {
        return teamName2;
    }

    public Integer getScore1() {
        return score1;
    }

    public Integer getScore2() {
        return score2;
    }
}
