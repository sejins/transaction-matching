package com.jingeore.chatting;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class ChattingMessage {

    @Id @GeneratedValue
    private Long id;

    private LocalDateTime writeTime;

    private Long writerId;

    private String message;
}
