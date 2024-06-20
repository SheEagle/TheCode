package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.UserPostStatistics;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserPostStatisticsMapper extends BaseMapper<UserPostStatistics> {
    @Select("SELECT * FROM user_post_statistics WHERE user_id = #{id}")
    UserPostStatistics queryById(@Param("id") int id);
}
