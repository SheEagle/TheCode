package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.dto.UserPostStatistics;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.StatisticsVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountMapper;
import com.example.mapper.AccountPrivacyMapper;
import com.example.mapper.UserPostStatisticsMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 账户信息处理相关服务
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    AmqpTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    FlowUtils flow;

    @Resource
    AccountPrivacyMapper privacyMapper;
    @Resource
    AccountDetailsMapper detailsMapper;
    @Resource
    UserPostStatisticsMapper statisticsMapper;

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     *
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("Incorrect user name or password");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     *
     * @param type    类型
     * @param email   邮件地址
     * @param address 请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailVerifyCode(String type, String email, String address) {
        synchronized (address.intern()) {
            if (!this.verifyLimit(address))
                return "Requests are frequent. Please try again later.";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码注册账号操作，需要检查验证码是否正确以及邮箱、用户名是否存在重名
     *
     * @param info 注册基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailAccount(EmailRegisterVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if (code == null) return "Please get the verification code first";
        if (!code.equals(info.getCode())) return "Wrong code, please check and re-enter";
        if (this.existsAccountByEmail(email)) return "This e-mail address is already registered";
        String username = info.getUsername();
        if (this.existsAccountByUsername(username))
            return "This username is already used by someone else, please change it again!";
        String password = passwordEncoder.encode(info.getPassword());
        Account account = new Account(null, info.getUsername(),
                password, email, Const.ROLE_DEFAULT, null, new Date());
        if (!this.save(account)) {
            return "Internal error, registration failed";
        } else {
            this.deleteEmailVerifyCode(email);
            privacyMapper.insert(new AccountPrivacy(account.getId()));
            AccountDetails details = new AccountDetails();
            details.setId(account.getId());
            detailsMapper.insert(details);
            return null;
        }
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     *
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if (verify != null) return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "Update failed, please contact the administrator";
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     *
     * @param info 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if (code == null) return "Please get the verification code first";
        if (!code.equals(info.getCode())) return "Wrong code, please check and re-enter";
        return null;
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    @Override
    public String modifyEmail(int id, EmailModifyVO vo) {
        String email = vo.getEmail();
        String code = getEmailVerifyCode(email);

        if (code == null)
            return "Please get the verification code first";
        if (!code.equals(vo.getCode()))
            return "Wrong code, please check and re-enter";
        this.deleteEmailVerifyCode(email);
        Account account = this.findAccountByNameOrEmail(email);
        if (account != null && account.getId() != id)
            return "This email address is tied to another account!";
        this.update()
                .set("email", email)
                .eq("id", id)
                .update();

        return null;
    }

    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id", id).one().getPassword();
        if (!passwordEncoder.matches(vo.getPassword(), password)) {
            return "The original password is wrong, please re-enter it!";
        }
        boolean success = this.update().
                eq("id", id).
                set("password", passwordEncoder.encode(vo.getNew_password())).
                update();

        return success ? null : "An unknown error has occurred";
    }

    @Override
    public StatisticsVO getStatistics(int id) {
        UserPostStatistics userPostStatistics = statisticsMapper.queryById(id);
        StatisticsVO vo = new StatisticsVO();
        vo.setTotalCollects(userPostStatistics.getTotalCollects());
        vo.setTotalLikes(userPostStatistics.getTotalLikes());
        vo.setTotalComments(userPostStatistics.getTotalComments());
        return vo;

    }


    /**
     * 移除Redis中存储的邮件验证码
     *
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis中存储的邮件验证码
     *
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     *
     * @param address 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    /**
     * 通过用户名或邮件地址查找用户
     *
     * @param text 用户名或邮件
     * @return 账户实体
     */
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    /**
     * 查询指定用户名的用户是否已经存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }
}
