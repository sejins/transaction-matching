package com.jingeore.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductForm {

    private String title;

    private String description;

    private Long offerPrice;
}
