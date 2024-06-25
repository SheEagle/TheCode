package com.example.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.entity.vo.request.EmailModifyVO;
import com.example.entity.vo.request.PrivacySaveVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountPrivacyVO;
import com.example.entity.vo.response.AccountVO;
import com.example.entity.vo.response.StatisticsVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.Const;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountDetailsService accountDetailsService;

    @MockBean
    private AccountPrivacyService accountPrivacyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void statistics() throws Exception {
        StatisticsVO statisticsVO = new StatisticsVO();
        given(accountService.getStatistics(anyInt())).willReturn(statisticsVO);

        mockMvc.perform(get("/api/user/statistics")
                        .requestAttr(Const.ATTR_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void info() throws Exception {
        Account account = new Account();
        given(accountService.findAccountById(anyInt())).willReturn(account);

        mockMvc.perform(get("/api/user/info")
                        .requestAttr(Const.ATTR_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void details() throws Exception {
        AccountDetails details = new AccountDetails();
        given(accountDetailsService.findAccountDetailsById(anyInt())).willReturn(details);

        mockMvc.perform(get("/api/user/details")
                        .requestAttr(Const.ATTR_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void saveDetails() throws Exception {
        DetailsSaveVO vo = new DetailsSaveVO();
        given(accountDetailsService.saveAccountDetails(anyInt(), any())).willReturn(true);

        mockMvc.perform(post("/api/user/save-details")
                        .requestAttr(Const.ATTR_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\": \"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void modifyEmail() throws Exception {
        EmailModifyVO vo = new EmailModifyVO();
        given(accountService.modifyEmail(anyInt(), any())).willReturn(null);

        mockMvc.perform(post("/api/user/modify-email")
                        .requestAttr(Const.ATTR_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void changePassword() throws Exception {
        ChangePasswordVO vo = new ChangePasswordVO();
        given(accountService.changePassword(anyInt(), any())).willReturn(null);

        mockMvc.perform(post("/api/user/change-password")
                        .requestAttr(Const.ATTR_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"oldPass\", \"newPassword\": \"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void savePrivacy() throws Exception {
        PrivacySaveVO vo = new PrivacySaveVO();

        mockMvc.perform(post("/api/user/save-privacy")
                        .requestAttr(Const.ATTR_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\": \"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

//    @Test
//    void privacy() throws Exception {
//        AccountPrivacyVO privacyVO = new AccountPrivacyVO();
//        given(accountPrivacyService.accountPrivacy(anyInt())).willReturn(privacyVO);
//
//        mockMvc.perform(get("/api/user/privacy")
//                        .requestAttr(Const.ATTR_USER_ID, 1))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.data").exists());
//    }
}
