package ru.samsung.nba_stats;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {PlayerInRoster.class}, version = 1)
public abstract class MyRoomPlayerInRosterDataBase extends RoomDatabase{
    public abstract PlayerInRosterDao playerInRosterDao();
    private static volatile MyRoomPlayerInRosterDataBase INSTANCE;
    static MyRoomPlayerInRosterDataBase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (MyRoomPlayerInRosterDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyRoomPlayerInRosterDataBase.class, "player_in_roster").build();

                }
            }
        }
        return INSTANCE;
    }
}
