package ru.samsung.nba_stats;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDao {
    @Insert
    void insert(GameR gameR);
    @Insert
    void insertAll(GameR ... gameRS);
    @Delete
    void delete(GameR gameR);
    @Update
    void updateGame(GameR gameR);
    @Query("SELECT * FROM gameR WHERE uid LIKE :uid LIMIT 1")
    GameR findById(int uid);
    @Query("SELECT * FROM gameR WHERE date LIKE :date LIMIT 1")
    GameR findByDate(String date);
    @Query("DELETE FROM gameR")
    void nukeTable();
    @Query("SELECT * FROM gameR WHERE date LIKE :date")
    List<GameR> loadAllByDate(String date);
    @Query("SELECT * FROM gameR")
    List<GameR> getAll();


    @Insert
    void insertMultipleGames(List<GameR> insertList);


    @Query("SELECT * FROM gameR WHERE uid IN (:gameIds)")
    List<GameR> loadAllByIds(int[]gameIds);
}
