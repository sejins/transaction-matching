package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
import com.jingeore.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    public void createNewAccount(SignUpForm signUpForm) {

        Account newAccount = saveNewAccount(signUpForm);

        //TODO 인증 메일 전송하는 기능 -> 현재는 콘솔에 로그 출력!
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account newAccount = new Account();
        newAccount.setNickname(signUpForm.getNickname());
        newAccount.setEmail(signUpForm.getEmail());
        newAccount.setPassword(passwordEncoder.encode(signUpForm.getPassword())); // 비밀번호는 암호화해서 저장. -> bcrypt 해싱 알고리즘사용.
        newAccount.setEmailConfirmToken(UUID.randomUUID().toString());// 계정을 만들때 랜덤한 토큰값을 생성한다.
        return accountRepository.save(newAccount);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText("/check-email-token?token="+newAccount.getEmailConfirmToken()+"&email="+newAccount.getEmail());
        simpleMailMessage.setTo(newAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    public void confirmAccount(Account account) {
        account.setRegDate(LocalDateTime.now());
        account.setEmailVerified(true);
    }
}
