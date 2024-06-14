package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.QuestionVO;
import com.example.entity.vo.response.AnswerVO;
import com.example.service.ChatService;
import com.example.utils.Const;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.entity.dto.Chat;
import com.example.entity.dto.ChatSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/sessions")
    public List<ChatSession> getAllChatSessions(@RequestParam Long userId) {
        return chatService.getAllChatSessionsByUserId(userId);
    }

    @GetMapping("/sessions/{sessionId}")
    public List<Chat> getAllChatsBySessionId(@PathVariable Long sessionId) {
        return chatService.getAllChatsBySessionId(sessionId);
    }


    @PostMapping("/ask")
    public RestBean<AnswerVO> askQuestion(@Valid @RequestBody QuestionVO vo,
                                          @RequestAttribute(Const.ATTR_USER_ID) int id) {
        String question = vo.getQuestion();
        return RestBean.success(chatService.askQuestion(id, question));
    }

    @PostMapping("/sessions")
    public ChatSession createChatSession(@RequestBody ChatSession chatSession, @RequestParam Long userId) {
        chatSession.setUserId(userId);
        return chatService.saveChatSession(chatSession);
    }


}


