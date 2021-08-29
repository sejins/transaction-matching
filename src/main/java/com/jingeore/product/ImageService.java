package com.jingeore.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    public final String PRODUCT_IMAGE_PATH = "C:\\images\\feed\\";

    public void addNewImage(List<MultipartFile> images, Long id) throws IOException {
        List<String> imagePaths = saveInLocal(images);
        saveImages(imagePaths, id);

    }

    private void saveImages(List<String> imagePaths, Long id) {
        Optional<Product> byId = productRepository.findById(id);
        Product product = byId.orElseThrow();

        for(String imagePath : imagePaths){
            ProductImage newImage = new ProductImage();
            newImage.setImagePath("/images/"+imagePath);
            product.getImages().add(newImage);
            imageRepository.save(newImage);
        }
    }

    private List<String> saveInLocal(List<MultipartFile> images) throws IOException {

        List<String> paths = new ArrayList<>();
        for(MultipartFile image : images){
            Date date = new Date();
            StringBuilder sb = new StringBuilder();

            if(!image.isEmpty()){
                sb.append(date.getTime());
                sb.append(image.getOriginalFilename());

                File dst = new File(PRODUCT_IMAGE_PATH + sb.toString());
                paths.add(sb.toString());
                image.transferTo(dst);
            }
        }
        return paths;
    }
}
