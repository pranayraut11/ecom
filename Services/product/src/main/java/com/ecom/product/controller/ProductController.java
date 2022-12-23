package com.ecom.product.controller;

import com.ecom.product.dto.ProductDTO;
import com.ecom.product.service.specification.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecom.shared.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
    public void create(@RequestParam("files")MultipartFile[] files,@RequestParam("product")String product) throws JsonProcessingException {
        for (MultipartFile file:files) {
            Assert.hasLength(file.getOriginalFilename(),"Please select file to upload");
        }
        ProductDTO productDTO = objectMapper.readValue(product,ProductDTO.class) ;
        productDTO.setImages(List.of(files));
        productService.create(productDTO);
    }

}
