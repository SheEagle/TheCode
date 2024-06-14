package com.example.entity.dto;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("chat_sessions")
public class ChatSession {
    @TableId
    private Long id;
    private Long userId;
    private String sessionName;
    private Timestamp createdAt;
}

