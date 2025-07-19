package com.example.ecom.Ecom.controller;

import com.example.ecom.Ecom.model.Product;
import com.example.ecom.Ecom.service.ProductService;
import com.example.ecom.Ecom.service.S3Service;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    private final ProductService service;

    private final S3Service s3Service;

    public ProductController(ProductService service, S3Service s3Service) {
        this.service = service;
        this.s3Service = s3Service;
    }

    @GetMapping("/")
    public String greet() {
        return "Hello from ecom app";
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            return ResponseEntity.ok(service.getProducts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching products: " + e.getMessage());
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        try {
            Product product = service.getProduct(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching product: " + e.getMessage());
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestPart("file") MultipartFile file,
                                        @RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("price") BigDecimal price,
                                        @RequestParam("category") String category,
                                        @RequestParam("releaseDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date releaseDate,
                                        @RequestParam("available") boolean available,
                                        @RequestParam("quantity") int quantity) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Error image not found");
            }

            String imageUrl = s3Service.uploadFile(file);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setAvailable(available);
            product.setQuantity(quantity);
            product.setReleaseDate(releaseDate);
            product.setImageUrl(imageUrl);

            Product savedProduct = service.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding product: " + e.getMessage());
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id,
                                           @RequestPart(value = "file", required = false) MultipartFile file,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "price", required = false) BigDecimal price,
                                           @RequestParam(value = "category", required = false) String category,
                                           @RequestParam(value = "releaseDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date releaseDate,
                                           @RequestParam(value = "available", required = false) Boolean available,
                                           @RequestParam(value = "quantity", required = false) Integer quantity) {
        try {
            Product existingProduct = service.getProduct(id);

            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error product not exist");
            }

            if (name != null && !name.trim().isEmpty()) {
                existingProduct.setName(name);
            }

            if (description != null) {
                existingProduct.setDescription(description);
            }

            if (price != null) {
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    return ResponseEntity.badRequest()
                            .body("Error: Price must be greater than 0");
                }
                existingProduct.setPrice(price);
            }

            if (category != null && !category.trim().isEmpty()) {
                existingProduct.setCategory(category);
            }

            if (releaseDate != null) {
                existingProduct.setReleaseDate(releaseDate);
            }

            if (available != null) {
                existingProduct.setAvailable(available);
            }

            if (quantity != null) {
                if (quantity < 0) {
                    return ResponseEntity.badRequest()
                            .body("Error: Quantity cannot be negative");
                }
                existingProduct.setQuantity(quantity);
            }

            if (file != null && !file.isEmpty()) {
                String imageUrl = s3Service.uploadFile(file);
                existingProduct.setImageUrl(imageUrl);
            } else {
                existingProduct.setImageUrl(existingProduct.getImageUrl());
            }

            Product updatedProduct = service.updateProduct(existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            Product product = service.getProduct(id);

            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error product not found");
            }

            service.deleteProduct(id);
            return ResponseEntity.ok("Product is deleted successufully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting product: " + e.getMessage());
        }
    }

}
