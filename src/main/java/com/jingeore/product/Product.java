package com.jingeore.product;

import com.jingeore.account.Account;
import com.jingeore.chatting.ChattingMessage;
import com.jingeore.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private LocalDateTime registrationDate;

    private Long offerPrice;

    @Enumerated(EnumType.STRING) // Enumerated를 통해서 타입을 STRING으로 지정해줘야 DB에 문자열에 해당하는 값이 저장이 된다. 안그러면 ORDINAL 값이 저장이 되는데 이는 좋지않음.
    private ProductStatus status;

    @OneToMany
    private List<ProductImage> images; // 이미지 테이블과 일대다 관계로 이미지를 관리한다. -> JPA 관점에서 단방향 관계로 이미지는 Product 도메인에서만 참조한다.

    @ManyToOne
    private Account seller; // 판매자와는 다대일 관계 (양방향 관계)

    @ManyToMany
    private Set<Account> buyerOffers = new HashSet<>(); // 현재 상품에 대해서 들어온 매칭 요청 (양방향 관계)

    @ManyToOne
    private Account buyer; // 구매자와는 다대일 관계(양방향 관계)

    @ManyToOne
    private Zone zone; // 상품의 판매 지역

    @OneToMany
    private List<ChattingMessage> chattings = new ArrayList<>();

    public String getPriceByWon() {
        DecimalFormat formatter = new DecimalFormat("###,###");
        return formatter.format(this.offerPrice)+"원";
    }

    public Set<Account> getBuyerOffers() {
        return buyerOffers;
    }

    public boolean canBeChangedToDealing(Account account) {
        return buyer.equals(account);
    }

    public void cancelMatching() {
        this.status = ProductStatus.NONE;
        this.buyer = null;
    }

    public boolean inProgress() {
        return this.status == ProductStatus.MATCHING || this.status == ProductStatus.IN_REQUEST || this.status == ProductStatus.DEALING;
    }

    public void completeMatching() {
        this.status = ProductStatus.COMPLETE;
        //this.buyer = null;
    }
}
