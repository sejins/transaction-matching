package com.jingeore.chatting;

import com.jingeore.product.Product;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class ChattingMessage {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Product product;

    private LocalDateTime writeTime;

    private Long writerId;

    private String message;
}
