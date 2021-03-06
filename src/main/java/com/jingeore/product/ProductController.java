package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.AccountRepository;
import com.jingeore.account.AccountService;
import com.jingeore.account.CurrentUser;
import com.jingeore.account.form.SignUpForm;
import com.jingeore.chatting.ChattingMessage;
import com.jingeore.chatting.ChattingMessageForm;
import com.jingeore.matching.FinishedMatchingInfo;
import com.jingeore.matching.ReviewResultForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);
        model.addAttribute(new ProductForm());
        return "product/form";
    }

    @PostMapping("/new-product")
    public String newProduct(@CurrentUser Account account, Model model, ProductForm productForm, Errors errors, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "product/form";
        }
        log.info(productForm.getDescription());
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
        log.info("????????????");
        log.info(product.getDescription());
        model.addAttribute("myAccount", myAccount);

        // image path list for product info page
        List<String> images = productService.getImagePathListForInfo(product);
        model.addAttribute("images",images);
        log.info(images.toString());
        return "product/info";
    }

    // TODO POST??? ??? ????????????????????? ????????? ????????? ??? ?????? ??????
    @GetMapping("/favorite/{id}")
    public String favoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.addFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","?????? ???????????? ?????? ???????????????.");
        return "redirect:/product/" + id;
    }

    @GetMapping("/defavorite/{id}")
    public String defavoriteProduct(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Product product = productService.getProduct(id);
        accountService.removeFavoriteProduct(account, product);
        redirectAttributes.addFlashAttribute("message","?????? ????????? ?????? ???????????????.");
        return "redirect:/product/" + id;
    }

    @PostMapping("/matching-offer/{id}")
    public String offerMatching(@CurrentUser Account account, Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        model.addAttribute(account);
        accountService.addMatchingOffer(account, id); // ??????????????? ?????? ?????? ????????? ??????.
        productService.addMatchingOffer(id, account); // ????????? ????????? ?????? ?????? ????????? ?????? -> ???????????? ??????????????? ????????? ???????????? ????????? ?????? ????????? ???????????? ????????? ?????? ????????? ????????????.
        redirectAttributes.addFlashAttribute("message","?????? ????????? ???????????????.");

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
        redirectAttributes.addFlashAttribute("message", "????????? ?????????????????????.");
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

//    @PostMapping("/cancel-matching/{productId}/{buyerId}")
//    public String cancelMatching(@CurrentUser Account account, @PathVariable Long productId, @PathVariable Long buyerId) {
//        productService.cancelMatching(productId);
//        return "redirect:/current-matching/sell";
//    }

    @PostMapping("/request-dealing/{productId}")
    public String requestDealing(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (!product.getBuyer().equals(accountRepository.findByNickname(account.getNickname())))
            throw new IllegalArgumentException("?????? ????????? ???????????? ??? ??? ????????????.");
        productService.requestDealing(product);
        redirectAttributes.addFlashAttribute("message", "?????? ????????? ??????????????? ?????????????????????.");
        return "redirect:/current-matching/detail/" + productId;
    }

    @PostMapping("/confirm-dealing-request/{productId}")
    public String confirmDealingRequest(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (!product.getSeller().equals(accountRepository.findByNickname(account.getNickname())))
            throw new IllegalArgumentException("????????????????????? ???????????? ??? ??? ????????????.");
        productService.confirmDealingRequest(product);
        redirectAttributes.addFlashAttribute("message", "??????????????? ??????????????????.");
        return "redirect:/current-matching/detail/" + productId;
    }

//    @GetMapping("/complete-matching/{productId}")
//    public String completeMatching(@CurrentUser Account account, @PathVariable Long productId, RedirectAttributes redirectAttributes) {
//        productService.completeMatching(productId, account);
//        redirectAttributes.addFlashAttribute("reviewMessage", "??????");
//        return "redirect:/current-matching/sell";
//    }

    @GetMapping("/chatting/{productId}")
    public String chatting(@CurrentUser Account account, @PathVariable Long productId, Model model) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        Product product = productRepository.findById(productId).orElseThrow();
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);
        model.addAttribute(product);
        ChattingMessageForm form = new ChattingMessageForm();
        form.setProductId(productId);
        form.setWriterId(myAccount.getId());
        model.addAttribute(form);
        model.addAttribute("chattings", productService.getAllChatting(product));
        return "matching/chatting";
    }

    @PostMapping("/chatting/new-message")
    public String sendNewMessage(@CurrentUser Account account, Model model, ChattingMessageForm chattingMessageForm) {
        Product product = productRepository.findById(chattingMessageForm.getProductId()).orElseThrow();
        Account myAccount = accountRepository.findById(chattingMessageForm.getWriterId()).orElseThrow();
        Long writerId = chattingMessageForm.getWriterId();
        String message = chattingMessageForm.getMessage();
        productService.saveNewMessage(product, writerId, message);
        List<ChattingMessage> chattings = productService.getAllChatting(product);
        model.addAttribute("chattings", chattings);
        model.addAttribute("myAccount", myAccount);
        model.addAttribute("product", product);

        return "matching/chatting :: #list";
    }

    // ????????? ???????????? ?????? ???????????? ?????? ?????????
    @PostMapping("/review/{productId}")
    public String writeReview(@CurrentUser Account account, Model model, @PathVariable Long productId, String flag) {
        model.addAttribute(account);
        model.addAttribute("flag", flag);

        Product product = productRepository.findById(productId).orElseThrow();
        Account opposite;
        if (product.getBuyer().getId() == account.getId()) {
            opposite = product.getSeller();
        } else {
            opposite = product.getBuyer();
        }

        model.addAttribute("opposite", opposite);
        model.addAttribute("product", product);
        model.addAttribute("reviewResultForm", new ReviewResultForm());

        if (flag.equals("true")) { // ????????????
//           Long fmId = productService.completeMatching(productId, account);
//           model.addAttribute("fmId", fmId);
            log.info("true!!!!!!!");
        } else { // ?????? ??????
//          Long fmId = productService.cancelMatching(productId);
//          model.addAttribute("fmId", fmId);
            log.info("false!!!!!!!");
        }
        return "matching/review";
    }

    // ????????? ?????? ???????????? ???????????? ?????? ?????????
    @GetMapping("/review/{productId}/{oppositeId}/{fmId}")
    public String writeReviewOnFinished(@CurrentUser Account account, Model model, @PathVariable Long productId, @PathVariable Long oppositeId, @PathVariable Long fmId) {
        model.addAttribute(account);
        Account opposite = accountRepository.findById(oppositeId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        model.addAttribute("opposite", opposite);
        model.addAttribute("product", product);
        model.addAttribute("reviewResultForm", new ReviewResultForm());
        model.addAttribute("fmId",fmId);
        return "matching/review";
    }

    @GetMapping("/current-matching/finished")
    public String currentMatchingFinished(@CurrentUser Account account, Model model) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("myAccount", myAccount);
        List<FinishedMatchingInfo> finishedList = myAccount.getFinishedMatchings().stream().map(fm ->
            FinishedMatchingInfo.builder().product(productRepository.findById(fm.getProductId()).orElseThrow())
                    .opposite(accountRepository.findById(fm.getOppositeId()).orElseThrow()).finishedMatchingId(fm.getId())
                    .finishedMatchingId(fm.getId())
                    .status(fm.getStatus())
                    .build()
        ).collect(Collectors.toList());
        model.addAttribute("finishedList", finishedList);
        return "matching/current-matchings-finished";
    }

    @PostMapping("/review/add/{oppositeId}/{fmId}")
    public String reviewTest(@CurrentUser Account account, @PathVariable Long oppositeId, @PathVariable Long fmId, @ModelAttribute ReviewResultForm reviewResultForm) {
        log.info(reviewResultForm.getPositive1());
        log.info(reviewResultForm.getPositive2());
        log.info(reviewResultForm.getPositive3());
        log.info(reviewResultForm.getPositive4());
        log.info(reviewResultForm.getPositiveEtc());
        log.info(reviewResultForm.getNegative1());
        log.info(reviewResultForm.getNegative2());
        log.info(reviewResultForm.getNegative3());
        log.info(reviewResultForm.getNegative4());
        log.info(reviewResultForm.getNegativeEtc());

        Account opposite = accountRepository.findById(oppositeId).orElseThrow();
        accountService.addReview(opposite, reviewResultForm);
        accountService.finishMatching(account, fmId);
        return "redirect:/";
    }

    @GetMapping("/review/skip/{fmId}")
    public String skipReview(@CurrentUser Account account, @PathVariable Long fmId) {
        accountService.finishMatching(account, fmId);
        return "redirect:/";
    }
}
