package com.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class TopicDetailsVO {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    User user;
    Interact interact;
    Long comments;

    @Data
    @AllArgsConstructor
    public static class Interact {
        Boolean like;
        Boolean collect;
    }


    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        String introduction;
        boolean gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
