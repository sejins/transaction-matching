package com.jingeore.product;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
}
