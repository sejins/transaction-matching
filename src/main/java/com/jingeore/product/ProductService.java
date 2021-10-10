package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.AccountRepository;
import com.jingeore.account.AccountService;
import com.jingeore.chatting.ChattingMessage;
import com.jingeore.chatting.ChattingMessageRepository;
import com.jingeore.zone.Zone;
import com.jingeore.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ZoneRepository zoneRepository;
    private final ChattingMessageRepository chattingMessageRepository;

    public Product createNewProduct(Account account, ProductForm productForm) {
        Product newProduct = modelMapper.map(productForm, Product.class);
        newProduct.setStatus(ProductStatus.NONE);
        newProduct.setRegistrationDate(LocalDateTime.now());
        accountService.addProduct(account, newProduct);
        newProduct.setSeller(account);
        Zone sellingZone = zoneRepository.findById(productForm.getZoneId()).orElseThrow();
        newProduct.setZone(sellingZone);
        return productRepository.save(newProduct);
    }

    public Product getProduct(Long id) {
        Optional<Product> productById = productRepository.findById(id);
        return productById.orElseThrow();
    }

    public void addMatchingOffer(Long id, Account account) {
        Account buyer = accountRepository.findByNickname(account.getNickname());
        Optional<Product> byId = productRepository.findById(id);
        Product product = byId.orElseThrow();
        product.getBuyerOffers().add(buyer);
    }

    public void cancelOffer(Product product, Account offeror) {
        product.getBuyerOffers().remove(offeror);
        accountService.cancelOffer(offeror, product);
    }

    public void confirmMatchingOffer(Product product, Account offeror) {
        cancelOffer(product, offeror); // 현재 존재하는 매칭 요청은 이제 삭제.
        product.setBuyer(offeror);
        product.setStatus(ProductStatus.MATCHING);
        accountService.confirmMatchingOffer(offeror, product);
    }

    public void cancelMatching(Long productId, Long buyerId) {
        Product product = productRepository.findById(productId).orElseThrow();
        Account buyer = accountRepository.findById(buyerId).orElseThrow();
        product.cancelMatching();
        buyer.endMatching(product);
    }

    public void requestDealing(Product product) {
        product.setStatus(ProductStatus.IN_REQUEST);
    }

    public void confirmDealingRequest(Product product) {
        product.setStatus(ProductStatus.DEALING);
    }

    public void completeMatching(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        Account buyer = accountRepository.findByNickname(product.getBuyer().getNickname());
        product.completeMatching();
        buyer.endMatching(product);
    }

    public void saveNewMessage(Product product, Long writerId, String message) {
        if (message.equals("")) return;

        ChattingMessage newMessage = ChattingMessage.builder()
                .writeTime(LocalDateTime.now())
                .product(product)
                .writerId(writerId)
                .message(message)
                .build();
        product.getChattings().add(chattingMessageRepository.save(newMessage));
    }

    public List<ChattingMessage> getAllChatting(Product product) {

        List<ChattingMessage> chattingList = product.getChattings();
        chattingList.sort(new Comparator<ChattingMessage>() {
            @Override
            public int compare(ChattingMessage o1, ChattingMessage o2) {
                return o1.getWriteTime().compareTo(o2.getWriteTime());
            }
        });
        return chattingList;
    }

    public List<String> getImagePathListForInfo(Product product) {
        // 뷰에서 경로 인식이 잘 안되는 문제 때문에!
        List<String> images = product.getImages().stream().map(ProductImage::getImagePath)
                .map(path -> path.replace("/images/", ""))
                .collect(Collectors.toList());
        return images;
    }
}
