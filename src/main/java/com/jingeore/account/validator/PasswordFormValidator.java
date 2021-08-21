package com.jingeore.account.validator;

import com.jingeore.account.AccountRepository;
import com.jingeore.account.form.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator{

    public final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) { // 검증할 내용 (패스워드 값과 패스워들 입력 값이 동일한지!!)

        PasswordForm passwordForm = (PasswordForm)object;
        if(!passwordForm.getPassword().equals(passwordForm.getPasswordConfirm())){
            errors.rejectValue("passwordConfirm","wrong.value","패스워드가 일치하지 않습니다.");
        }
    }

}
