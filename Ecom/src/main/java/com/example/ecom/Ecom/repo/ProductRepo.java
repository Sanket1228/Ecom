package com.example.ecom.Ecom.repo;

import com.example.ecom.Ecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepo extends JpaRepository<Product, Integer>{
}
