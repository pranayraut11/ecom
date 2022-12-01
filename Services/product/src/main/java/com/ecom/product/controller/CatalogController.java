package com.ecom.product.controller;

import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product.entity.Product;

@RestController
@CrossOrigin("*")
@RequestMapping("product")
public class CatalogController extends BaseController<Product> {

}
