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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ImageService imageService;

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
        return "redirect:/product/addImages/" + newProduct.getId();
    }

    @GetMapping("product/addImages/{id}")
    public String addProductImages(@CurrentUser Account account, Model model, @PathVariable Long id){
        model.addAttribute(account);
        model.addAttribute(id);
        return "product/images";
    }

    @PostMapping("product/addImages/{id}")
    public String saveProductImages(@CurrentUser Account account, Model model, @PathVariable Long id, @RequestParam("images") List<MultipartFile> images) throws IOException {
        System.out.println(images);

        imageService.addNewImage(images, id);

        return "redirect:/product/" + id;
    }

    @GetMapping("/imageTest/{id}")
    public String imageTest(@CurrentUser Account account, Model model, @PathVariable Long id){
        Product product = productService.getProduct(id);
        model.addAttribute(account);
        List<String> images = product.getImages().stream().map(ProductImage::getImagePath).collect(Collectors.toList());
        images.add("");
        model.addAttribute("images",images);
        return "product/test";
    }

    @GetMapping("/product/{id}")
    public String productInfo(@CurrentUser Account account, Model model, @PathVariable Long id){
        Product product = productService.getProduct(id);
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute(product);
        model.addAttribute("myAccount", myAccount);

        List<String> images = product.getImages().stream().map(ProductImage::getImagePath).collect(Collectors.toList());
        model.addAttribute("images",images);
        model.addAttribute("image",images.get(0));
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

//    @GetMapping("/imageAddTest")
//    public String addImage(@CurrentUser Account account, Model model){
//        model.addAttribute(account);
//        return "product/addImage";
//    }
//
//    @PostMapping("/upload-image")
//    public String uploadImages(@CurrentUser Account account, Model model, @RequestParam("files") List<MultipartFile> files) throws IOException {
//        Date date = new Date();
//        StringBuilder sb = new StringBuilder();
//
//
//    }
}
