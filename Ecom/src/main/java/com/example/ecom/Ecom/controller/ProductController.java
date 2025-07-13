package com.example.ecom.Ecom.controller;

import com.example.ecom.Ecom.model.Product;
import com.example.ecom.Ecom.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String greet() {
        return "Hello from ecom app";
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getProducts();
    }

}
