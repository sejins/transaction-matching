package com.jingeore.account;

import com.jingeore.domain.Account;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest { // 테스트시에도 DB에 값을 반영하기 때문에, 각 테스트 메소드마다 생성된 값을 지워줘야한다.

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("회원 가입 페이지 GET 요청")
    @Test
    void signUp_view() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 - 성공")
    @Test
    void signUp_success() throws Exception {
        String nickname = "sejin123";
        String password = "123456789";
        mockMvc.perform(post("/sign-up")
                .param("nickname",nickname)
                .param("email","test123@email.com")
                .param("password",password)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(accountRepository.existsByNickname("sejin123"));
        //인코딩된 패스워드와 동일한지 확인
        assertNotEquals(accountRepository.findByNickname(nickname).getPassword(), password);
    }

    @DisplayName("회원 가입 - 실패 - 닉네임이 이미 존재하는 경우")
    @Test
    void signUp_fail_equalNickname() throws Exception {

        Account testAccount = new Account();
        testAccount.setNickname("sejin123");
        testAccount.setEmail("test@email.com");
        testAccount.setPassword("1111");
        accountRepository.save(testAccount);

        mockMvc.perform(post("/sign-up")
                .param("nickname","sejin123")
                .param("email","test123@email.com")
                .param("password","123456789")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 - 실패 - 이메일이 이미 존재하는 경우")
    @Test
    void signUp_fail_equalEmail() throws Exception {

        Account testAccount = new Account();
        testAccount.setNickname("test");
        testAccount.setEmail("test123@email.com");
        testAccount.setPassword("1111");
        accountRepository.save(testAccount);

        mockMvc.perform(post("/sign-up")
                .param("nickname","sejin123")
                .param("email","test123@email.com")
                .param("password","123456789")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }
}