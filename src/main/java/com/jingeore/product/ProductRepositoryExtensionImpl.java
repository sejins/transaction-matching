package com.jingeore.product;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ProductRepositoryExtensionImpl extends QuerydslRepositorySupport implements ProductRepositoryExtension{
    public ProductRepositoryExtensionImpl() {
        super(Product.class);
    }
    @Override
    public List<Product> findByKeyword(String keyword) {
        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product).where(product.status.eq(ProductStatus.MATCHING)
        .and(product.title.containsIgnoreCase(keyword)));
        return query.fetch();
    }
}
