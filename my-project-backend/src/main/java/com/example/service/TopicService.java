package com.example.service;

import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicDetailsVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;

import java.util.List;

public interface TopicService {
    List<TopicType> listTypes();

    String createTopic(int uid, TopicCreateVO vo);

    List<TopicPreviewVO> listTopicByPage(int page, int type);

    List<TopicTopVO> listTopTopics();

    TopicDetailsVO getTopic(int tid);

    void interact(Interact interact, boolean state);

    List<TopicPreviewVO> listTopicCollects(int uid);


}
