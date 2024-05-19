package ru.samsung.nba_stats;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Team.class}, version = 1)
public abstract class MyRoomTeamsDataBase extends RoomDatabase {
    public abstract TeamsDao teamsDao();
    private static volatile MyRoomTeamsDataBase INSTANCE;
    static MyRoomTeamsDataBase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (MyRoomTeamsDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyRoomTeamsDataBase.class, "teams_table").build();

                }
            }
        }
        return INSTANCE;
    }
}
