package com.bruhdev.myapplication.ChatDB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    void insertChatMessage(ChatMessage chatMessage);

    @Query("SELECT * FROM chat_history WHERE address = :address ORDER BY timestamp ASC")
    List<ChatMessage> getChatHistory(String address);

    @Query("DELETE FROM chat_history WHERE address = :address")
    void deleteChatHistory(String address);
}

