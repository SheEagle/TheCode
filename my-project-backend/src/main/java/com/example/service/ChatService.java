package com.example.service;

import com.example.entity.dto.Chat;
import com.example.entity.dto.ChatSession;
import com.example.entity.vo.response.AnswerVO;

import java.util.List;

public interface ChatService {
    public List<Chat> getAllChatsBySessionId(Long sessionId);

    public Chat saveChat(Chat chat);

    public List<ChatSession> getAllChatSessionsByUserId(Long userId);

    public ChatSession saveChatSession(ChatSession chatSession);

    AnswerVO askQuestion(int userId, String question);
}
