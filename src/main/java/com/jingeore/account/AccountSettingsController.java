package com.jingeore.account;

import com.jingeore.account.form.NicknameForm;
import com.jingeore.account.form.PasswordForm;
import com.jingeore.account.validator.NicknameFormValidator;
import com.jingeore.account.validator.PasswordFormValidator;
import com.jingeore.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequestMapping("/settings")
@Controller
@RequiredArgsConstructor
public class AccountSettingsController {

    private final NicknameFormValidator nicknameFormValidator;
    private final PasswordFormValidator passwordFormValidator;
    private final AccountService accountService;

    @InitBinder("nicknameForm")
    void nicknameInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @InitBinder("passwordForm")
    void passwordInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwordFormValidator);
    }

    @GetMapping("/nickname")
    public String updateNicknameForm(@CurrentUser Account account, Model model){

        NicknameForm nicknameForm = new NicknameForm();
        nicknameForm.setNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("nicknameForm", nicknameForm);

        return "settings/nickname";
    }

    @PostMapping("/nickname")
    public String updateNickname(@CurrentUser Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors, Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/nickname";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message","닉네임을 성공적으로 수정했습니다.");
        return "redirect:/settings/nickname";
    }

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentUser Account account, Model model){
        PasswordForm passwordForm = new PasswordForm();
        model.addAttribute(account);
        model.addAttribute(passwordForm);

        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors,Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message","패스워드를 성공적으로 수정했습니다.");
        return "redirect:/settings/password";
    }

}
