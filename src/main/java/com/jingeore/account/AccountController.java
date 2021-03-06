package com.jingeore.account;

import com.jingeore.account.form.SignUpForm;
import com.jingeore.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController{

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @GetMapping("/sign-up")
    public String signUpView(Model model){
        model.addAttribute(new SignUpForm()); // 폼 클래스를 뷰에 제공 -> 타임리프로 인식해서 사용.
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(Model model, @Valid @ModelAttribute SignUpForm signUpForm, Errors errors){

        signUpFormValidator.validate(signUpForm,errors); //signUpForm에 대해서 검증 -> InitBinder를 사용해서 검증을수행할 수도 있다.
        if(errors.hasErrors()){
            return "account/sign-up";
        }
        Account newAccount = accountService.createNewAccount(signUpForm);
        model.addAttribute("email",newAccount.getEmail());
        return "account/check-email";

    }

    @PostMapping("/resend-email")
    public String resendEmail(String email, Model model){
        //메일을 재전송
        Account account = accountRepository.findByEmail(email);
        if(accountService.canSendSignUpConfirmEmail(account)){
            accountService.resendSignUpConfirmEmail(account);
        }
        else{
            model.addAttribute("error","can't send email");
        }
        model.addAttribute("email",email);
        return "account/check-email";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(Model model, String email, String token){
        Account account = accountRepository.findByEmail(email);
        if(account == null){
            model.addAttribute("error","wrong email");
            return "account/confirmed-email";
        }
        if(!account.getEmailConfirmToken().equals(token)){
            model.addAttribute("error","wrong token");
            return "account/confirmed-email";
        }
        model.addAttribute("nickname",account.getNickname());
        accountService.confirmAccount(account);
        accountService.login(account); //이메일 인증이 완료된 계정은 로그인을 시켜준다.
        model.addAttribute(account);
        return "account/confirmed-email";
    }

    @GetMapping("/profile/{nickname}")
    public String profilePage(@CurrentUser Account account, Model model, @PathVariable String nickname){
        Account profileAccount = accountRepository.findByNickname(nickname);
        if(profileAccount == null){
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute(account);
        model.addAttribute("profileAccount",profileAccount);
        model.addAttribute("itsMyProfile",nickname.equals(account.getNickname()));
        return "account/profile";
    }
}
