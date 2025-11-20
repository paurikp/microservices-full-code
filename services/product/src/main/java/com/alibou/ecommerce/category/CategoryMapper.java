package com.alibou.ecommerce.category;

public class CategoryMapper {
	// ...existing code...
	public static CategoryDto toDto(Category c) {
		if (c == null) return null;
		return CategoryDto.builder()
				.id(c.getId())
				.name(c.getName())
				.description(c.getDescription())
				.build();
	}

	public static Category toEntity(CategoryDto d) {
		if (d == null) return null;
		return Category.builder()
				.id(d.getId())
				.name(d.getName())
				.description(d.getDescription())
				.build();
	}
}

