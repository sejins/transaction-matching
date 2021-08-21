package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;

    @GetMapping("/new-product")
    public String newProductForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new ProductForm());
        return "product/form";
    }

    @PostMapping("/new-product")
    public String newProduct(@CurrentUser Account account, Model model, ProductForm productForm, Errors errors, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "product/form";
        }
        productService.createNewProduct(account, productForm);
        redirectAttributes.addFlashAttribute("message","상품을 성공적으로 등록하였습니다.");
        return "redirect:/"; // TODO 상품 상세 페이지로 리다이렉션 해야한다.
    }
}
