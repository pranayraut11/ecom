package com.ecom.product.controller;

import com.ecom.product.dto.ProductDTO;
import com.ecom.product.service.specification.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecom.shared.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping()
    public void create(MultipartFile[] files, String productJson) throws JsonProcessingException {
        ProductDTO productDTO = objectMapper.readValue(productJson,ProductDTO.class) ;
        productDTO.setImages(List.of(files));
        productService.create(productDTO);
    }

}
