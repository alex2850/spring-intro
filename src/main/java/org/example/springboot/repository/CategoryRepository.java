package org.example.springboot.repository;

import org.example.springboot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends
        JpaRepository<Category, Long> {
}
