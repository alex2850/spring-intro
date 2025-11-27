package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.example.springboot.exception.EntityNotFoundException;
import org.example.springboot.mapper.CategoryMapper;
import org.example.springboot.model.Category;
import org.example.springboot.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found id=" + id));
        return categoryMapper.toDto(c);
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category saved = categoryRepository.save(categoryMapper.toModel(dto));
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, UpdateCategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found id=" + id));

        categoryMapper.updateModel(dto, category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found id=" + id);
        }
        categoryRepository.deleteById(id);
    }
}
