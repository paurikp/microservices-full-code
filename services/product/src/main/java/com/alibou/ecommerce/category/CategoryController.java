package com.alibou.ecommerce.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService service;

	@GetMapping
	public List<CategoryDto> getAll() {
		return service.listAll();
	}

	@GetMapping("/{id}")
	public CategoryDto get(@PathVariable Integer id) {
		return service.getById(id);
	}

	@PostMapping
	public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
		CategoryDto created = service.create(dto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public CategoryDto update(@PathVariable Integer id, @RequestBody CategoryDto dto) {
		return service.update(id, dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}

