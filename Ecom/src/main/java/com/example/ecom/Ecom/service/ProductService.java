package com.example.ecom.Ecom.service;

import com.example.ecom.Ecom.model.Product;
import com.example.ecom.Ecom.repo.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    ProductRepo repo;

    public ProductService(ProductRepo repo) {
        this.repo = repo;
    }

    public List<Product> getProducts() {
        return repo.findAll();
    }
}
