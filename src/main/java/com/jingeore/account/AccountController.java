package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
import com.jingeore.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController{

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;



    @GetMapping("/sign-up")
    public String signUpView(Model model){
        model.addAttribute(new SignUpForm()); // 폼 클래스를 뷰에 제공 -> 타임리프로 인식해서 사용.
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(Model model, @Valid @ModelAttribute SignUpForm signUpForm, Errors errors){

        signUpFormValidator.validate(signUpForm,errors); //signUpForm에 대해서 검증

        if(errors.hasErrors()){
            return "account/sign-up";
        }

        accountService.createNewAccount(signUpForm);

        return "redirect:/";
        // POST -> REDIRECT -> GET 패턴
    }
}
