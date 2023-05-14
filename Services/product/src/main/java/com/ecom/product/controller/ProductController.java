package com.ecom.product.controller;

import com.ecom.product.constant.ExceptionCode;
import com.ecom.product.dto.ProductDTO;
import com.ecom.product.service.specification.ProductService;
import com.ecom.shared.common.validation.DtoValidator;
import com.ecom.shared.common.validation.FileValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    @Operation(summary = "Create product")
    @PostMapping()
    public void create(@RequestParam("files")MultipartFile[] files,@RequestParam("product")String product) throws JsonProcessingException {
        FileValidation.isEmpty(List.of(files),ExceptionCode.AUTH_401_01.getErrorCode());
        ProductDTO productDTO = objectMapper.readValue(product,ProductDTO.class) ;
        validator.validate(productDTO);
        productService.create(productDTO,List.of(files));
    }

    @Operation(summary = "Get all products")
    @GetMapping()
    public List<ProductDTO> getAll(){
       return productService.getAll();
    }

    @Operation(summary = "Delete product and its images")
    @DeleteMapping()
    public void deleteProduct(@RequestBody List<String> ids,String productId) throws IOException {
        productService.delete(ids,productId);
    }
}
