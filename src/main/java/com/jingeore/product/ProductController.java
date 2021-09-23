package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.AccountRepository;
import com.jingeore.account.AccountService;
import com.jingeore.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Controller
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
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

    @GetMapping("/product/{id}")
    public String productInfo(@CurrentUser Account account, Model model, @PathVariable Long id){
        Product product = productService.getProduct(id);
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute(product);
        model.addAttribute("images",product.getImages());
        model.addAttribute("myAccount", myAccount);

        List<String> images = product.getImages().stream().map(ProductImage::getImagePath).collect(Collectors.toList());
        model.addAttribute("images",images);
        model.addAttribute("image",images.get(0));
        return "product/info";
    }

    // TODO POST가 더 적절해보이는데 시간이 남으면 이 부분 수정
    @GetMapping("/favorite/{id}")
    public String favoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.addFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","관심 상품으로 등록 되었습니다.");
        return "redirect:/product/" + id;
    }

    @GetMapping("/defavorite/{id}")
    public String defavoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.removeFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","관심 상품이 해제 되었습니다.");
        return "redirect:/product/" + id;
    }

    @PostMapping("/matching-offer/{id}")
    public String offerMatching(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        model.addAttribute(account);
        accountService.addMatchingOffer(account, id); // 구매자에게 매칭 요청 관계를 추가.
        productService.addMatchingOffer(id, account); // 판매자 상품에 매칭 요청 관계를 추가 -> 여기에는 추가적으로 판매자 계정에는 새로운 매칭 요청이 생겼다는 알림을 위한 필드를 생성한다.
        redirectAttributes.addFlashAttribute("message","매칭 요청을 보냈습니다.");

        return "redirect:/product/" + id;
    }

    @GetMapping("/matching-list")
    public String getMatchingList(@CurrentUser Account account, Model model){
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);
        return "product/matching-list";
    }

    @GetMapping("/matching-details/{id}")
    public String matchingDetails(@CurrentUser Account account, Model model, @PathVariable Long id) {
        Optional<Product> byId = productRepository.findById(id);
        Product product = byId.orElseThrow();
        model.addAttribute(account);
        model.addAttribute(product);
        return "/product/matching-details";
    }

    @GetMapping("/cancel-matching-offer/{productId}/{offerorId}")
    public String cancelOffer(@CurrentUser Account account, Model model, @PathVariable Long productId, @PathVariable Long offerorId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        Account offeror = accountRepository.findById(offerorId).orElseThrow();
        productService.cancelOffer(product, offeror);
        redirectAttributes.addFlashAttribute("message", "요청을 취소하였습니다.");
        return "redirect:/matching-details/" + product.getId();
    }

    @GetMapping("/confirm-matching-offer/{productId}/{offerorId}")
    public String confirmOffer(@CurrentUser Account account,
                               Model model,
                               @PathVariable Long productId,
                               @PathVariable Long offerorId,
                               RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        Account offeror = accountRepository.findById(offerorId).orElseThrow();
        productService.confirmMatchingOffer(product, offeror);

        return "redirect:/current-matching/buy";
    }

    @GetMapping("/current-matching/buy")
    public String currentMatchingBuy(@CurrentUser Account account, Model model) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);

        return "matching/current-matchings-buy";
    }

    @GetMapping("/current-matching/sell")
    public String currentMatchingSell(@CurrentUser Account account, Model model) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);

        return "matching/current-matchings-sell";
    }

    @GetMapping("/current-matching/detail/{id}")
    public String currentMatchingDetail(@CurrentUser Account account, Model model, @PathVariable Long id) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        Product matchingProduct = productRepository.findById(id).orElseThrow();
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);
        model.addAttribute(matchingProduct);
        return "matching/current-matching-detail";
    }

    @PostMapping("/cancel-matching/{productId}/{buyerId}")
    public String cancelMatching(@CurrentUser Account account, @PathVariable Long productId, @PathVariable Long buyerId) {
        productService.cancelMatching(productId, buyerId);
        return "redirect:/current-matching/sell";
    }

    @PostMapping("/request-dealing/{productId}")
    public String requestDealing(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (!product.getBuyer().equals(accountRepository.findByNickname(account.getNickname())))
            throw new IllegalArgumentException("거래 요청은 구매자만 할 수 있습니다.");
        productService.requestDealing(product);
        redirectAttributes.addFlashAttribute("message", "거래 요청이 성공적으로 완료되었습니다.");
        return "redirect:/current-matching/detail/" + productId;
    }

    @PostMapping("/confirm-dealing-request/{productId}")
    public String confirmDealingRequest(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (!product.getSeller().equals(accountRepository.findByNickname(account.getNickname())))
            throw new IllegalArgumentException("거래요청수락은 판매자만 할 수 있습니다.");
        productService.confirmDealingRequest(product);
        redirectAttributes.addFlashAttribute("message", "거래요청을 수락했습니다.");
        return "redirect:/current-matching/detail/" + productId;
    }

    @GetMapping("/complete-matching/{productId}")
    public String completeMatching(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
        productService.completeMatching(productId);
        redirectAttributes.addFlashAttribute("reviewMessage", "여기");
        return "redirect:/current-matching/sell";
    }
}
