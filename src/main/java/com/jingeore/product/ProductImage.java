package com.jingeore.product;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class ProductImage {

    @Id @GeneratedValue
    private Long id;

    private String imagePath;

}
