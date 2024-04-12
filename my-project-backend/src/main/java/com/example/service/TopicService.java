package com.example.service;

import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;

import java.util.List;

public interface TopicService {
    List<TopicType> listTypes();

    String createTopic(int uid, TopicCreateVO vo);
}
