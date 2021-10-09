package com.jingeore.chatting;

import com.jingeore.product.Product;
import lombok.*;
import org.apache.tomcat.jni.Local;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String change() {
        String time = writeTime.format(DateTimeFormatter.ofPattern("a hh:mm "));
        if (writeTime.getYear() == LocalDateTime.now().getYear()) {
            if (writeTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                time += "(오늘)";
            } else if (writeTime.getDayOfMonth() + 1 == LocalDateTime.now().getDayOfMonth()) {
                time += "(어제)";
            } else {
                time += writeTime.format(DateTimeFormatter.ofPattern("(MM월 dd일)"));
            }
        } else {
            time += writeTime.format(DateTimeFormatter.ofPattern("(yyyy-MM-dd)"));
        }
        return time;
    }
}
