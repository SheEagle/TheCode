package com.example.service;

import com.example.entity.dto.Chat;
import com.example.entity.dto.ChatSession;
import com.example.entity.vo.request.ChatSessionVO;
import com.example.entity.vo.response.AnswerVO;

import java.util.Date;
import java.util.List;

public interface ChatService {
    List<Chat> getAllChatsBySessionId(int sessionId);

    Chat saveChat(Chat chat);

    List<ChatSession> getAllChatSessionsByUserId(int userId);

    ChatSession saveChatSession(String sessionName, Date createdAt, int userId);

    AnswerVO askQuestion(int userId, int sessionId, String question);

    ChatSession renameSession(int id, String sessionName);

}
