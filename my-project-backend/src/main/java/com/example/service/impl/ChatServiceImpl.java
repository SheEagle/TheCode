package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Chat;
import com.example.entity.dto.ChatSession;
import com.example.entity.vo.response.AnswerVO;
import com.example.mapper.ChatMapper;
import com.example.mapper.ChatSessionMapper;
import com.example.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.Constants;

import java.util.Date;
import java.util.List;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {
    @Value("${spring.ai.key}")
    private String apiKey;
    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    public List<Chat> getAllChatsBySessionId(Long sessionId) {
        return chatMapper.selectList(new QueryWrapper<Chat>().eq("session_id", sessionId));
    }

    @Override
    public Chat saveChat(Chat chat) {
        chatMapper.insert(chat);
        return chat;
    }

    @Override
    public List<ChatSession> getAllChatSessionsByUserId(Long userId) {
        return chatSessionMapper.selectList(new QueryWrapper<ChatSession>().eq("user_id", userId));
    }

    @Override
    public ChatSession saveChatSession(ChatSession chatSession) {
        chatSessionMapper.insert(chatSession);
        return chatSession;
    }

    @Override
    public AnswerVO askQuestion(int userId, String question) {
        Constants.apiKey = apiKey;
        AnswerVO vo = new AnswerVO();
        try {
            Generation gen = new Generation();
            MessageManager msgManager = new MessageManager(10);
            Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个STEM领域专家，你只回答与STEM相关的问题，不要回答其他问题！").build();
            Message userMsg = Message.builder().role(Role.USER.getValue()).content(question).build();
            msgManager.add(systemMsg);
            msgManager.add(userMsg);
            QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).build();
            GenerationResult result = gen.call(param);
            GenerationOutput output = result.getOutput();
            Message message = output.getChoices().get(0).getMessage();
            String answer = message.getContent();

            Chat chat = new Chat();
            chat.setUserId(userId);
            chat.setQuestion(question);
            chat.setAnswer(answer);
            this.save(chat);

            vo.setAnswer(answer);
            vo.setTime(new Date());
            return vo;
        } catch (Exception e) {
            vo.setAnswer("STEM专家现在不在线，请稍后再试～");
            vo.setTime(new Date());
            return vo;
        }

    }
}



