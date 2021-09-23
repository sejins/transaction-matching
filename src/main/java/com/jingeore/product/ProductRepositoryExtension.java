package com.jingeore.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryExtension {
    Page<Product> findByKeyword(String keyword, Pageable pageable);
}
