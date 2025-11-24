package org.example.springboot.service;

import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto dto);

    CategoryDto update(Long id, UpdateCategoryRequestDto dto);

    void deleteById(Long id);
}
