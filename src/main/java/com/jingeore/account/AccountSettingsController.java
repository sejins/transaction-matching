package com.jingeore.account;

import com.jingeore.account.form.NicknameForm;
import com.jingeore.account.validator.NicknameFormValidator;
import com.jingeore.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/settings")
@Controller
@RequiredArgsConstructor
public class AccountSettingsController {

    private final NicknameFormValidator nicknameFormValidator;

    @InitBinder("nicknameForm")
    void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping("/nickname")
    public String updateNicknameForm(@CurrentUser Account account, Model model){

        NicknameForm nicknameForm = new NicknameForm();
        model.addAttribute(account);
        model.addAttribute(nicknameForm);

        return "settings/nickname";
    }
}
