package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;

    public Product createNewProduct(Account account, ProductForm productForm) {
        Product newProduct = modelMapper.map(productForm, Product.class);
        newProduct.setStatus(ProductStatus.NONE);
        newProduct.setRegistrationDate(LocalDateTime.now());
        accountService.addProduct(account, newProduct);
        newProduct.setSeller(account);
        return productRepository.save(newProduct);
    }

    public Product getProduct(Long id) {
        Optional<Product> productById = productRepository.findById(id);
        return productById.orElseThrow();
    }
}