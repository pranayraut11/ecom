package com.ecom.catelog.controller;

import com.ecom.catelog.entity.Product;
import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("catelog")
public class CatelogController extends BaseController<Product> {

}
