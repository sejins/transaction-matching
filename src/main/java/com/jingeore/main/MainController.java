package com.jingeore.main;

import com.jingeore.account.CurrentUser;
import com.jingeore.account.Account;
import com.jingeore.product.Product;
import com.jingeore.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProductRepository productRepository;

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

    @GetMapping("/search/product")
    public String searchProduct(@CurrentUser Account account, @PageableDefault(size = 6, page = 0) Pageable pageable, String keyword, Model model) {
        Page<Product> productPage = productRepository.findByKeyword(keyword, pageable);
        if (account != null) {
            model.addAttribute(account);
        }
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}
