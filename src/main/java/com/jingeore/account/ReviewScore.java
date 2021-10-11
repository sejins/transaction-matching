package com.jingeore.account;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewScore {

    @Id @GeneratedValue
    private Long id;

    private int positive1;

    private int positive2;

    private int positive3;

    private int positive4;

    private int negative1;

    private int negative2;

    private int negative3;

    private int negative4;

    public String positive1() {
        return "친절하고 매너가 좋아요";
    }
    public String positive2() {
        return "상품의 상태가 설명과 동일해요";
    }
    public String positive3() {
        return "시간 약속을 잘 지켜요";
    }
    public String positive4() {
        return "좋은 상품을 착한 가격에 팔아요";
    }
    public String negative1() {
        return "불친절해요";
    }
    public String negative2() {
        return "상품의 상태가 설명과 달라요";
    }
    public String negative3() {
        return "시간 약속을 지키지 않아요";
    }
    public String negative4() {
        return "거래 당일날 연락이 안돼요";
    }

    public int calculateScore() {
        int baseScore = 50;
        int plus = positive1 + positive2 + positive3 + positive4;
        int minus = negative1 + negative2 + negative3 + negative4;
        return baseScore + plus - minus;
    }
}
