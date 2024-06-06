package com.bruhdev.myapplication.ChatDB;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ChatMessage.class}, version = 1)
public abstract class ChatDatabase extends RoomDatabase {
    private static volatile ChatDatabase INSTANCE;

    public abstract ChatMessageDao chatMessageDao();

    public static ChatDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ChatDatabase.class, "chat_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
