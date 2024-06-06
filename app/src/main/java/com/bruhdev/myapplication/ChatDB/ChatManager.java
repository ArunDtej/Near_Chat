package com.bruhdev.myapplication.ChatDB;

import com.bruhdev.myapplication.Util;

import java.util.List;

public class ChatManager {
    public static ChatDatabase cd = ChatDatabase.getDatabase(Util.appConext);
    public static ChatMessageDao cmd = cd.chatMessageDao();

    public static void insert(String msg, boolean sentTo, String address) {
        new Thread(() -> {
            cmd.insertChatMessage(getChatMessage(msg, sentTo, address));
        }).start();
    }

    public static List<ChatMessage> getMessages(String address){
        return cmd.getChatHistory(address);
    }

    public static ChatMessage getChatMessage(String msg, boolean sentTo, String address) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(msg);
        chatMessage.setSentTo(sentTo);
        chatMessage.setAddress(address);
        chatMessage.setTimestamp(System.currentTimeMillis());
        return chatMessage;
    }



}
