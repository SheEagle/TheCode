package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;

/*
这段代码定义了一个接口 `AccountDetailsService`，
它继承自 `IService<AccountDetails>` 接口，
表示提供对 `AccountDetails` 实体的服务。
接口中包含了两个方法：`findAccountDetailsById` 用于根据 id 查找账户详情，
`saveAccountDetails` 用于保存或更新账户详情。
 */

public interface AccountDetailsService extends IService<AccountDetails> {
    AccountDetails findAccountDetailsById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVO vo);
}

