package ru.samsung.nba_stats;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GameR.class}, version = 1)
public abstract class MyRoomDataBase extends RoomDatabase {
    public abstract GameDao gameDao();
    private static volatile MyRoomDataBase INSTANCE;
    static MyRoomDataBase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (MyRoomDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyRoomDataBase.class, "game_table").build();

                }
            }
        }
        return INSTANCE;
    }
}
