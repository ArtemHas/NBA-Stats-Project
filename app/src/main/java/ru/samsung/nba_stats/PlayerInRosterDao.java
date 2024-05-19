package ru.samsung.nba_stats;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayerInRosterDao {
    @Insert
    void insert(PlayerInRoster playerInRoster);
    @Insert
    void insertAll(PlayerInRoster ... playerInRosters);
    @Delete
    void delete(PlayerInRoster playerInRoster);
    @Update
    void updateGame(PlayerInRoster playerInRoster);
    @Query("SELECT * FROM playerinroster WHERE uid LIKE :uid LIMIT 1")
    PlayerInRoster findById(int uid);
    @Query("SELECT * FROM playerinroster WHERE playerURL LIKE :URL LIMIT 1")
    PlayerInRoster findPlayerByURL(String URL);
    @Query("DELETE FROM playerinroster")
    void nukeTable();
    @Query("SELECT * FROM playerinroster WHERE rosterURL LIKE :URL")
    List<PlayerInRoster> loadAllByRosterURL(String URL);
    @Query("SELECT * FROM playerinroster")
    List<PlayerInRoster> getAll();
    @Insert
    void insertMultiplePlayers(List<PlayerInRoster> insertList);

    @Query("SELECT * FROM playerinroster WHERE uid IN (:playersIds)")
    List<PlayerInRoster> loadAllByIds(int[]playersIds);
}
