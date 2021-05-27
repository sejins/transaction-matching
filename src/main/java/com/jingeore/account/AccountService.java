package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
import com.jingeore.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public void createNewAccount(SignUpForm signUpForm) {

        Account newAccount = new Account();
        newAccount.setNickname(signUpForm.getNickname());
        newAccount.setEmail(signUpForm.getEmail());
        newAccount.setPassword(signUpForm.getPassword()); // TODO 패스워드는 암호화해서 저장해야한다. -> 해싱으로
        newAccount.setRegDate(LocalDateTime.now());

        //TODO 가입 시 제출한 이메일로 회원가입 확인 메일을 보내는 기능

        accountRepository.save(newAccount);
    }
}
