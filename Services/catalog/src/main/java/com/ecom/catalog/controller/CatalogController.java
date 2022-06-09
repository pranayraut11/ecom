package com.ecom.catalog.controller;

import com.ecom.catalog.entity.Product;
import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("catelog")
public class CatalogController extends BaseController<Product> {

}
