package com.example.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_chat_session")
public class ChatSession {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String sessionName;
    private Date createdAt;
}

