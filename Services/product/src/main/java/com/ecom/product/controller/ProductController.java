package com.ecom.product.controller;

import com.ecom.product.constant.ExceptionCode;
import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import com.ecom.product.service.specification.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ecom.shared.validation.DtoValidator;
import org.ecom.shared.validation.FileValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin("*")
@RequestMapping("product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DtoValidator validator;


    @PostMapping()
    public void create(@RequestParam("files")MultipartFile[] files,@RequestParam("product")String product) throws JsonProcessingException {
        FileValidation.isEmpty(List.of(files),ExceptionCode.AUTH_401_01.getErrorCode());
        ProductDTO productDTO = objectMapper.readValue(product,ProductDTO.class) ;
        validator.validate(productDTO);
        productService.create(productDTO,List.of(files));
    }

    @GetMapping()
    public List<ProductDTO> getAll(){
       return productService.getAll();
    }

}
