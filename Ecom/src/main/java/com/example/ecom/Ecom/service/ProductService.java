package com.example.ecom.Ecom.service;

import com.example.ecom.Ecom.model.Product;
import com.example.ecom.Ecom.repo.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepo repo;

    public ProductService(ProductRepo repo) {
        this.repo = repo;
    }

    public List<Product> getProducts() {
        return repo.findAll();
    }

    public Product getProduct(int id) {
        return repo.findById(id).orElse(null);
    }

    public Product addProduct(Product prod) {
        return repo.save(prod);
    }

    public Product updateProduct(Product existingProduct) {
        return repo.save(existingProduct);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }
}
