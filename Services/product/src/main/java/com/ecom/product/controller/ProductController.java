package com.ecom.product.controller;

import com.ecom.product.constant.ExceptionCode;
import com.ecom.product.dto.ProductDTO;
import com.ecom.product.service.specification.ProductService;
import com.ecom.shared.validation.DtoValidator;
import com.ecom.shared.validation.FileValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
