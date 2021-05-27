package com.jingeore.account.validator;

import com.jingeore.account.AccountRepository;
import com.jingeore.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator{// 이메일과 닉네임이 데이터베이스에 이미 저장되어 있는지 검증

    public final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class); // SignUpForm 에 대한 검증이 맞는지
    }

    @Override
    public void validate(Object object, Errors errors) { // 검증할 내용 (이메일 중복, 닉네임 중복)

        SignUpForm signUpForm = (SignUpForm)object;

        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email","invalid.email","이미 등록된 이메일입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname","invalid.nickname","이미 사용중인 닉네임입니다.");
        }
    }

}
