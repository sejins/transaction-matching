package com.jingeore.account.validator;

import com.jingeore.account.AccountRepository;
import com.jingeore.account.form.NicknameForm;

import com.jingeore.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator{

    public final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(NicknameForm.class); // NicknameForm 에 대한 검증이 맞는지
    }

    @Override
    public void validate(Object object, Errors errors) { // 검증할 내용 (이메일 중복, 닉네임 중복)

        NicknameForm nicknameForm = (NicknameForm)object;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if(byNickname!=null){
            errors.rejectValue("nickname","wrong.value","이미 사용중인 닉네임입니다.");
        }
    }

}
