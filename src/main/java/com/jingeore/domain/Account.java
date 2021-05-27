package com.jingeore.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    Long id;

    @Column(unique = true)
    String nickname;

    @Column(unique = true)
    String email;

    Boolean emailVerified = false;

    String emailConfirmToken;

    String password;

    @Lob @Basic(fetch = FetchType.EAGER)
    String profileImage;

    LocalDateTime regDate;

    Integer sellingMannerScore;

    Integer buyingMannerScore;

    Boolean isBanned = false;

    // TODO 알림 기능 개발할 때 필요에 따라서 필드를 생성하기
}
