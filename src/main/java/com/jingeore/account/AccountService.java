package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
import com.jingeore.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public void login(Account account) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                account.getNickname(), // principal
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // TODO 인증이 되었는지 테스트 코드에서 확인하기
        /*
        * 정석적인 방법 -> 하지만 이것은 평문 패스워드에 대해서 인증을 하는 방식이다. DB에 저장된 암호화된 패스워드로 인증을 가능하게 하려면 위의 방법을 사용해야한다.
        * 현재 매커니즘상으로는 패스워드의 평문에 접근할 수 없기 떄문에 일반적인 인증방식을 사용하지 못하는 것이다.
        * 현재는 DB에 저장되어 있는 패스워드(암호화된 값)를 통해서 인증을 수행하는 것이기 때문에 Security 내부적으로 사용되는 일부를 직접 사용한 것이다.
        * 기본적인 방법은 username으로 유저를 확인하고, 평문 패스워드와 암호화된 패스워드를 확인하는 과정을 거치게 된다.
        * 하지만 지금의 매커니즘은 이미 DB에 저장되어 있는 값으로 바로 인증을 해야하는 것이기 때문에, 시큐리티의 모든 인증과정을 수행할 필요가 없는 것이다.
        * 그래서 로그인 페이지에서 로그인을 할때는 평문 패스워드를 비교해야하기 때문에 정석적인 인증 방식을 사용하게 된다.
        */

//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                       username, password);
//
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authentication);
    }
}
