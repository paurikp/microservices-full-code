package com.alibou.ecommerce.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
	// id is included so controller can return created resource location
	private Integer id;
	private String name;
	private String description;
}

