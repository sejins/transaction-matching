package com.jingeore.account;

import com.jingeore.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AccountSettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }


    @DisplayName("닉네임 변경 - 성공")
    @Test
    @WithMockCustomUser("sejin")
    void updateNickname_success() throws Exception {
        mockMvc.perform(post("/settings/nickname")
                .param("nickname","jjinse")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/nickname"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("sejin");
        assertNull(account);
    }

    @DisplayName("닉네임 변경 - 실패")
    @Test
    @WithMockCustomUser("sejin")
    void updateNickname_fail() throws Exception {
        mockMvc.perform(post("/settings/nickname")
                .param("nickname","f")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/nickname"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));

        Account account = accountRepository.findByNickname("sejin");
        assertNotNull(account);
    }

    @DisplayName("패스워드 변경 - 성공")
    @Test
    @WithMockCustomUser("sejin")
    void updatePassword_success() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("password", "123123123")
                .param("passwordConfirm", "123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));
    }

    @DisplayName("패스워드 변경 - 실패")
    @Test
    @WithMockCustomUser("sejin")
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("password", "123123123")
                .param("passwordConfirm", "1111111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }
}