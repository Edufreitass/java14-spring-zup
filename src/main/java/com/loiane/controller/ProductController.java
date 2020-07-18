package com.loiane.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loiane.enums.ProductStatus;
import com.loiane.model.Product;
import com.loiane.record.ProductDTO;
import com.loiane.record.ProductRecord;
import com.loiane.repository.ProductRepository;
import com.loiane.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductRepository repository;
	
	private final ProductService service;

	// declaring record in class
	// it is necessary to use the @JsonProperty annotation
	// when you have a record as a response of a request
	record Response(@JsonProperty List<Product> list, @JsonProperty int total) {}
	
	// if dependency injection is necessary, it is good practice
	// to do the injection through the constructor
	public ProductController(ProductRepository repository, ProductService service) {
		this.repository = repository;
		this.service = service;
	}

	@GetMapping
	public @ResponseBody Response findAll() {
		var list = repository.findAll();
		return new Response(list, list.size());
	}
	
	@GetMapping("{id}")
	public @ResponseBody ProductRecord findById(@PathVariable Integer id) {
		return service.findById(id);
	}

	// using Java 14 record as DTO 
	/* @PostMapping
	public Product create(@RequestBody @Valid ProductDTO product) {
		return this.repository.save(product.toEntity());
	} */
	
	@PostMapping
	public ProductRecord create (@RequestBody @Valid ProductDTO product) {
		var status = switch (product.status()) {
			case 1 -> ProductStatus.ACTIVE;
			case 0 -> ProductStatus.INACTIVE;
			default -> throw new IllegalArgumentException("Invalid option");
		 };
		 return this.service.create(product.name(), status);
	}

}
