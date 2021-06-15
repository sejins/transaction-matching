package com.jingeore.account;

import com.jingeore.account.form.NicknameForm;
import com.jingeore.account.validator.NicknameFormValidator;
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
    private final AccountService accountService;

    @InitBinder("nicknameForm")
    void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping("/nickname")
    public String updateNicknameForm(@CurrentUser Account account, Model model){

        NicknameForm nicknameForm = new NicknameForm();
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
}
