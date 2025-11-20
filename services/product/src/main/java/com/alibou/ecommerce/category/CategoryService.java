package com.alibou.ecommerce.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository repository;

	public List<CategoryDto> listAll() {
		return repository.findAll().stream()
				.map(CategoryMapper::toDto)
				.collect(Collectors.toList());
	}

	public CategoryDto getById(Integer id) {
		return repository.findById(id)
				.map(CategoryMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Category not found: " + id));
	}

	public CategoryDto create(CategoryDto dto) {
		Category entity = CategoryMapper.toEntity(dto);
		entity.setId(null); // ensure new entity
		Category saved = repository.save(entity);
		return CategoryMapper.toDto(saved);
	}

	public CategoryDto update(Integer id, CategoryDto dto) {
		Category existing = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found: " + id));
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		Category saved = repository.save(existing);
		return CategoryMapper.toDto(saved);
	}

	public void delete(Integer id) {
		repository.deleteById(id);
	}
}

