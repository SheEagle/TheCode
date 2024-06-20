package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user_post_statistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostStatistics {
    private Integer userId;
    private String userName;
    private Integer totalLikes;
    private Integer totalCollects;
    private Integer totalComments;
}
