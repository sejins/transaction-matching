package com.jingeore.product;

import java.util.List;

public interface ProductRepositoryExtension {
    List<Product> findByKeyword(String keyword);
}
