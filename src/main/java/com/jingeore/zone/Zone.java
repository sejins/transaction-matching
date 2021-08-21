package com.jingeore.zone;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Zone {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city; // 영문 도시 이름

    @Column(nullable = false)
    private String localNameOfCity; // 한글 도시 이름

    @Column(nullable = true)
    private String province; // 지역자치 도

    @Override
    public String toString(){
        return String.format("%s(%s)/%s",localNameOfCity, city, province);
    }
}
