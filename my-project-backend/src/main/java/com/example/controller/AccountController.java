package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService service;

    @Resource
    AccountDetailsService detailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }

    @GetMapping("/details")
    // 声明处理 HTTP GET 请求，并且路径为 "/details"
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        // 定义方法名为 details，接受一个 id 参数，该参数来自请求属性 Const.ATTR_USER_ID
        // 返回类型为 RestBean<AccountDetailsVO>
        AccountDetails details = Optional.ofNullable(detailsService.findAccountDetailsById(id))
                // 使用 Optional 类处理可能为 null 的情况
                // 调用 findAccountDetailsById 方法根据提供的 id 查找用户的账户详情信息
                .orElseGet(AccountDetails::new);
        // 如果 findAccountDetailsById 返回的是 null，则创建一个新的 AccountDetails 对象
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
        // 返回一个成功的 RestBean 对象，其中包含了通过 asViewObject 方法转换的 AccountDetailsVO 对象
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid DetailsSaveVO vo) {
        boolean success = detailsService.saveAccountDetails(id, vo);
        return success ? RestBean.success() : RestBean.failure(400, "此用户名已被其他用户使用，请更换！");

    }


}
