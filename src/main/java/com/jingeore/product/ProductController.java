package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.AccountRepository;
import com.jingeore.account.AccountService;
import com.jingeore.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

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
        Product newProduct = productService.createNewProduct(account, productForm);
        redirectAttributes.addFlashAttribute("message","상품을 성공적으로 등록하였습니다.");
        return "redirect:/product/" + newProduct.getId();
    }

    @GetMapping("/product/{id}")
    public String productInfo(@CurrentUser Account account, Model model, @PathVariable Long id){
        Product product = productService.getProduct(id);
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute(product);
        model.addAttribute("myAccount", myAccount);

        return "product/info";
    }

    // TODO POST가 더 적절해보이는데 시간이 남으면 이 부분 수정
    @GetMapping("/favorite/{id}")
    public String favoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id,  RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.addFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","관심 상품으로 등록 되었습니다.");
        return "redirect:/product/" + id;
    }

    @GetMapping("/defavorite/{id}")
    public String defavoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id,  RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.removeFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","관심 상품이 해제 되었습니다.");
        return "redirect:/product/" + id;
    }
}
