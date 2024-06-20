package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("view_topic_hotness")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotness {
    private Integer topicId;
    private String title;
    private String content;
    private Integer uid;
    private Integer type;
    private Date time;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private Double hotness;
}
