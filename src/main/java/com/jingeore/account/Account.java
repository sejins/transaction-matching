package com.jingeore.account;

import com.jingeore.product.Product;
import com.jingeore.product.ProductController;
import com.jingeore.zone.Zone;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
@Slf4j
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private Boolean emailVerified = false;

    private String emailConfirmToken;

    private LocalDateTime emailSendTime = LocalDateTime.now().minusHours(1);

    private String password;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private LocalDateTime regDate;

    private Double sellingMannerScore = 5.0;

    private Double buyingMannerScore = 5.0;

    private Boolean isBanned = false;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>(); // 지역 도메인과 다대다 관계

    @OneToMany(mappedBy = "seller", fetch = FetchType.EAGER)
    private Set<Product> sellingProducts = new HashSet<>(); // 판매중인 상품과는 일대다 관계 (양방향 관계)

    // TODO 알림 기능 개발할 때 필요에 따라서 필드를 생성하기

    public boolean isNotMyProduct(Product product){
        log.info(this.sellingProducts.contains(product)+"");
        return !this.sellingProducts.contains(product);
    }
}