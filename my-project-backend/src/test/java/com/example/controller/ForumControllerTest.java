package com.example.controller;

import com.example.utils.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

import com.example.entity.RestBean;
import com.example.entity.dto.Interact;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumController.class)
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private TopicService topicService;

    @MockBean
    private ControllerUtils controllerUtils;

    @MockBean
    private FlowUtils flowUtils;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void weather() throws Exception {
        WeatherVO weatherVO = new WeatherVO();
        given(weatherService.fetchWeather(anyDouble(), anyDouble())).willReturn(weatherVO);

        mockMvc.perform(get("/api/forum/weather")
                        .param("longitude", "123.45")
                        .param("latitude", "67.89"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }


    @Test
    void listTypes() throws Exception {
        List<TopicTypeVO> types = List.of(new TopicTypeVO());
        given(topicService.listTypes()).willReturn(List.of());
        given(topicService.listTypes().stream().map(type -> type.asViewObject(TopicTypeVO.class)).toList()).willReturn(types);

        mockMvc.perform(get("/api/forum/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void createTopic() throws Exception {
        TopicCreateVO topicCreateVO = new TopicCreateVO();
        given(controllerUtils.messageHandle(any())).willReturn(RestBean.success());

        mockMvc.perform(post("/api/forum/create-topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Topic\", \"content\": \"Test Content\"}")
                        .requestAttr(Const.ATTR_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listTopic() {
    }

    @Test
    void topTopic() {
    }

    @Test
    void topic() {
    }

    @Test
    void interact() {
    }

    @Test
    void collects() {
    }

    @Test
    void updateTopic() {
    }

    @Test
    void deleteTopic() {
    }

    @Test
    void adminDeleteTopic() {
    }

    @Test
    void addComment() {
    }

    @Test
    void comments() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void adminDeleteComment() {
    }
}