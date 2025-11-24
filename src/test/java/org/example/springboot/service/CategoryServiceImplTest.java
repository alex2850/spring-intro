package org.example.springboot.service;

import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.example.springboot.exception.EntityNotFoundException;
import org.example.springboot.mapper.CategoryMapper;
import org.example.springboot.model.Category;
import org.example.springboot.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("findAll() — returns page of CategoryDto")
    void findAll_ShouldReturnPagedCategories() {
        Pageable pageable = PageRequest.of(0, 2);

        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("Food");

        Category c2 = new Category();
        c2.setId(2L);
        c2.setName("Books");

        Page<Category> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        CategoryDto dto1 = new CategoryDto(1L, "Food", "F-desc");
        CategoryDto dto2 = new CategoryDto(2L, "Books", "B-desc");

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(categoryMapper.toDto(c1)).thenReturn(dto1);
        Mockito.when(categoryMapper.toDto(c2)).thenReturn(dto2);

        List<CategoryDto> actual = categoryService.findAll(pageable).toList();

        assertEquals(2, actual.size());
        assertEquals(List.of(dto1, dto2), actual);
    }

    @Test
    @DisplayName("getById() — returns CategoryDto when exists")
    void getById_ShouldReturnCategory() {
        Long id = 10L;

        Category entity = new Category();
        entity.setId(id);
        entity.setName("Tech");
        entity.setDescription("desc");

        CategoryDto expected = new CategoryDto(id, "Tech", "desc");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        Mockito.when(categoryMapper.toDto(entity)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("getById() — throws EntityNotFoundException when id not found")
    void getById_ShouldThrowNotFound() {
        Long id = 999L;

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(id)
        );

        assertEquals("Category not found id=" + id, ex.getMessage());
    }


    @Test
    @DisplayName("save() — returns saved CategoryDto")
    void save_ShouldReturnSavedCategory() {
        CategoryDto dto = new CategoryDto(null, "Food", "desc");

        Category entity = new Category();
        entity.setName("Food");
        entity.setDescription("desc");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Food");
        saved.setDescription("desc");

        CategoryDto expected = new CategoryDto(1L, "Food", "desc");

        Mockito.when(categoryMapper.toModel(dto)).thenReturn(entity);
        Mockito.when(categoryRepository.save(entity)).thenReturn(saved);
        Mockito.when(categoryMapper.toDto(saved)).thenReturn(expected);

        CategoryDto actual = categoryService.save(dto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update() — updates existing category and returns updated dto")
    void update_ShouldReturnUpdatedDto() {
        Long id = 5L;

        UpdateCategoryRequestDto req = new UpdateCategoryRequestDto("NewName", "NewDesc");

        Category existing = new Category();
        existing.setId(id);
        existing.setName("Old");
        existing.setDescription("OldDesc");

        Category updated = new Category();
        updated.setId(id);
        updated.setName("NewName");
        updated.setDescription("NewDesc");

        CategoryDto expected = new CategoryDto(id, "NewName", "NewDesc");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.doAnswer(inv -> {
            UpdateCategoryRequestDto r = inv.getArgument(0);
            Category c = inv.getArgument(1);
            c.setName(r.name());
            c.setDescription(r.description());
            return null;
        }).when(categoryMapper).updateModel(req, existing);

        Mockito.when(categoryRepository.save(existing)).thenReturn(updated);
        Mockito.when(categoryMapper.toDto(updated)).thenReturn(expected);

        CategoryDto actual = categoryService.update(id, req);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update() — throws when entity is not found")
    void update_ShouldThrowNotFound_WhenIdInvalid() {
        Long id = 999L;
        UpdateCategoryRequestDto req = new UpdateCategoryRequestDto("X", "Y");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(id, req)
        );

        assertEquals("Category not found id=" + id, ex.getMessage());
    }

    @Test
    @DisplayName("deleteById() — deletes successfully")
    void deleteById_ShouldDelete() {
        Long id = 7L;

        Mockito.when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteById(id);

        Mockito.verify(categoryRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById() — throws when id does not exist")
    void deleteById_ShouldThrowNotFound() {
        Long id = 999L;

        Mockito.when(categoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deleteById(id)
        );

        assertEquals("Category not found id=" + id, ex.getMessage());
    }
}