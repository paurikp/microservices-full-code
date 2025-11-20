package com.alibou.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	// Optional helper finder used often by UI/logic
	Optional<Category> findByName(String name);
}

