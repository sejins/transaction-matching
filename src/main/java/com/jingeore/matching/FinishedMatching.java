package com.jingeore.matching;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class FinishedMatching {

    @Id
    @GeneratedValue
    private Long id;

    private Long productId;

    private Long oppositeId;

    @Enumerated(EnumType.STRING)
    private EndStatus status = EndStatus.CANCEL;
}
