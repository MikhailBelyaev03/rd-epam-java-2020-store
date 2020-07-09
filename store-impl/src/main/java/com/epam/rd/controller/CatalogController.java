package com.epam.rd.controller;

import com.epam.rd.dto.CatalogDTO;
import com.epam.rd.entity.Product;
import com.epam.rd.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CatalogController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/catalog")
    public CatalogDTO getCatalog(){
        CatalogDTO catalogDTO = new CatalogDTO();
        catalogDTO.products = (List<Product>)productRepository.findAll();
        return  catalogDTO;
    }

    @GetMapping("/catalog/{id}")
    public CatalogDTO getCatalogById(@PathVariable UUID id){
        CatalogDTO catalogDTO = new CatalogDTO();
        catalogDTO.product = productRepository.findById(id).orElse(new Product());

        return  catalogDTO;
    }
}
