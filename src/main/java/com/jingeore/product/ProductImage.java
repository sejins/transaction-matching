package com.jingeore.product;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class ProductImage {

    @Id @GeneratedValue
    private Long id;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String imagePath;

}
