package com.jingeore.main;

import com.jingeore.account.CurrentUser;
import com.jingeore.account.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String indexView(Model model, @CurrentUser Account account){
        if(account!=null){
            model.addAttribute(account); // 이 요청은 인증된 사용자와 인증되지 않은 사용자 모두 요청할 수 있기 떄문에, account가 null이 아닐 경우에만 model 객체에 념겨줘야한다.
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
