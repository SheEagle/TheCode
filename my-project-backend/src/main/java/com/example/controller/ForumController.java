package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicTypeVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Autowired
    ControllerUtils utils;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = service.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理位置与天气失败，请联系管理员") : RestBean.success(vo);

    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes() {
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.createTopic(id, vo));
    }




}
