package com.example.entity.vo.request;

import lombok.Data;

import java.util.Date;

@Data
public class ChatSessionVO {
    Date createdAt;
    String sessionName;
}
