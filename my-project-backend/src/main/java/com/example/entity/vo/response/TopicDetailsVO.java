package com.example.entity.vo.response;

import lombok.Data;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;

import java.util.Date;

@Data
public class TopicDetailsVO {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    User user;


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
