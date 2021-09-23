package com.jingeore.product;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ProductRepositoryExtensionImpl extends QuerydslRepositorySupport implements ProductRepositoryExtension{
    public ProductRepositoryExtensionImpl() {
        super(Product.class);
    }
    @Override
    public Page<Product> findByKeyword(String keyword, Pageable pageable) {
        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product).where(product.status.eq(ProductStatus.NONE)
        .and(product.title.containsIgnoreCase(keyword)));

        JPQLQuery<Product> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Product> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
