package com.example.productservice.controller;


import com.example.productservice.constan.UrlConstant;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.service.CategoryService;
import com.example.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(UrlConstant.API_V1_CATEGORY_INTERNAL)
public class InternalCategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/checkExists/{categoryId}")
    public ResponseEntity<Boolean> checkCategoryExists(@PathVariable Integer categoryId) {
        log.info("Checking if product exists with ID: {}", categoryId);
        boolean exists = categoryService.getCategoryById(categoryId) != null;
        return ResponseEntity.ok(exists);
    }

}