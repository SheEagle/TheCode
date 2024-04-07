package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.AccountDetails;
import org.apache.ibatis.annotations.Mapper;


/*
`AccountDetailsMapper` 是一个接口，
使用 `@Mapper` 注解表示它是一个 MyBatis 的映射器接口，
继承了 `BaseMapper<AccountDetails>` 接口，
提供了对 `AccountDetails` 实体类的基本数据库操作。
 */
@Mapper
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {

}


