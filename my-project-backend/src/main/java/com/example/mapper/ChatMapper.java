package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Chat;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
}

