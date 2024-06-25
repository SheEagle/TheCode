package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChatSessionVO;
import com.example.entity.vo.request.QuestionVO;
import com.example.entity.vo.request.RenameSessionVO;
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
    public RestBean<List<ChatSession>> getAllChatSessions(@RequestParam int userId) {
        return RestBean.success(chatService.getAllChatSessionsByUserId(userId));
    }


    @GetMapping("/sessions/{sessionId}")
    public RestBean<List<Chat>> getAllChatsBySessionId(@PathVariable int sessionId) {
        return RestBean.success(chatService.getAllChatsBySessionId(sessionId));
    }



    @PostMapping("/ask")
    public RestBean<AnswerVO> askQuestion(@Valid @RequestBody QuestionVO vo,
                                          @RequestAttribute(Const.ATTR_USER_ID) int id) {
        String question = vo.getQuestion();
        int sessionId = vo.getSessionId();
        return RestBean.success(chatService.askQuestion(id, sessionId, question));
    }



    @PostMapping("/create-session")
    public RestBean<ChatSession> createChatSession(@RequestBody ChatSessionVO vo,
                                                   @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return RestBean.success(chatService.saveChatSession(vo.getSessionName(), vo.getCreatedAt(), userId));
    }

    @PostMapping("/rename-session")
    public RestBean<ChatSession> renameSession(@RequestBody RenameSessionVO vo) {
        return RestBean.success(chatService.renameSession(vo.getId(), vo.getSessionName()));
    }


}


