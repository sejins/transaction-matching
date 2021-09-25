package com.jingeore.product;

import com.jingeore.zone.Zone;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductForm {

    private String title;

    private String description;

    private Long offerPrice;

    private Long zoneId;
}
