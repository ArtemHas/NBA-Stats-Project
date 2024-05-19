package ru.samsung.nba_stats;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TeamsDao {
    @Query("SELECT * FROM team")
    List<Team> getAllTeams();
    @Insert
    void insert(Team team);
    @Query("DELETE FROM team")
    void nukeTable();
    @Insert
    void insertMultipleTeams(List<Team> insertList);

}
