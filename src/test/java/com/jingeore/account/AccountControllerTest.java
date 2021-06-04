package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest { // 테스트시에도 DB에 값을 반영하기 때문에, 각 테스트 메소드마다 생성된 값을 지워줘야한다.

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;

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
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());

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

    @DisplayName("회원가입 이메일 인증 - 성공")
    @Test
    void check_email_token_success() throws Exception {

        Account newAccount = creatAccountForCheckEmailTokenTest();

        mockMvc.perform(get("/check-email-token?token="+newAccount.getEmailConfirmToken()+"&email="+newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/confirmed-email"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(authenticated());
        //TODO 로그인 기능 구현되면 이메일 인증 후 authenticated 한 상태인지 확인 필요.

        Account account = accountRepository.findByEmail(newAccount.getEmail());
        assertTrue(account.getEmailVerified());
        assertNotNull(account.getRegDate());
    }

    @DisplayName("회원가입 이메일 인증 - 실패")
    @Test
    void check_email_token_fail() throws Exception {

        Account newAccount = creatAccountForCheckEmailTokenTest();

        mockMvc.perform(get("/check-email-token?token="+newAccount.getEmailConfirmToken()+"&email=wrongEmail@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/confirmed-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated());

        Account account = accountRepository.findByEmail(newAccount.getEmail());
        assertFalse(account.getEmailVerified());
        assertNull(account.getRegDate());
    }

    private Account creatAccountForCheckEmailTokenTest() {
        Account account = new Account();
        account.setNickname("test123");
        account.setEmail("test123@naver.com");
        account.setEmailConfirmToken(UUID.randomUUID().toString());
        return accountRepository.save(account);
    }

    @DisplayName("인증 메일 재전송")
    @Test
    void resendConfirmEmail() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("sejin");
        signUpForm.setEmail("sejin123@test.com");
        signUpForm.setPassword("123123123");
        accountService.createNewAccount(signUpForm);

        mockMvc.perform(post("/resend-email")
                .param("email","sejin123@test.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("error"));

        mockMvc.perform(post("/resend-email") //5분이 지나기전에 인증 메일을 재전송하는 경우. 재전송할 수 없음!
                .param("email","sejin123@test.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("error"));
    }

}