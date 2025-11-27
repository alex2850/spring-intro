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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        Category fiction = new Category();
        fiction.setId(1L);
        fiction.setName("Food");

        Category fantasy = new Category();
        fantasy.setId(2L);
        fantasy.setName("Books");

        Page<Category> page = new PageImpl<>(List.of(fiction, fantasy), pageable, 2);

        CategoryDto fictionDto = new CategoryDto(1L, "Food", "F-desc");
        CategoryDto fantasyDto = new CategoryDto(2L, "Books", "B-desc");

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(fiction)).thenReturn(fictionDto);
        when(categoryMapper.toDto(fantasy)).thenReturn(fantasyDto);

        List<CategoryDto> actual = categoryService.findAll(pageable).toList();

        assertEquals(2, actual.size());
        assertEquals(List.of(fictionDto, fantasyDto), actual);

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(fiction);
        verify(categoryMapper).toDto(fantasy);
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

        when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(categoryMapper.toDto(entity)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(id);

        assertEquals(expected, actual);

        verify(categoryRepository).findById(id);
        verify(categoryMapper).toDto(entity);
    }

    @Test
    @DisplayName("getById() — throws EntityNotFoundException when id not found")
    void getById_WithNotExistID_ThrowNotFound() {
        Long id = 999L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(id));

        verify(categoryRepository).findById(id);
        verify(categoryMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("save() — returns saved CategoryDto")
    void saveCategory_WithValidRequestDto_Ok() {
        CategoryDto dto = new CategoryDto(null, "Food", "desc");

        Category entity = new Category();
        entity.setName("Food");
        entity.setDescription("desc");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Food");
        saved.setDescription("desc");

        CategoryDto expected = new CategoryDto(1L, "Food", "desc");

        when(categoryMapper.toModel(dto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(expected);

        CategoryDto actual = categoryService.save(dto);

        assertEquals(expected, actual);

        verify(categoryMapper).toModel(dto);
        verify(categoryRepository).save(entity);
        verify(categoryMapper).toDto(saved);
    }

    @Test
    @DisplayName("update() — updates existing category and returns updated dto")
    void updateCategory_WithValidRequestDto_Ok() {
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

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        doAnswer(inv -> {
            UpdateCategoryRequestDto r = inv.getArgument(0);
            Category c = inv.getArgument(1);
            c.setName(r.name());
            c.setDescription(r.description());
            return null;
        }).when(categoryMapper).updateModel(req, existing);

        when(categoryRepository.save(existing)).thenReturn(updated);
        when(categoryMapper.toDto(updated)).thenReturn(expected);

        CategoryDto actual = categoryService.update(id, req);

        assertEquals(expected, actual);

        verify(categoryRepository).findById(id);
        verify(categoryMapper).updateModel(req, existing);
        verify(categoryRepository).save(existing);
        verify(categoryMapper).toDto(updated);
    }

    @Test
    @DisplayName("update() — throws when entity is not found")
    void update_WhenIdInvalid_ThrowNotFound() {
        Long id = 999L;
        UpdateCategoryRequestDto req = new UpdateCategoryRequestDto("X", "Y");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(id, req));

        verify(categoryRepository).findById(id);
    }

    @Test
    @DisplayName("deleteById() — deletes successfully")
    void deleteById_ShouldDelete() {
        Long id = 7L;

        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteById(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById() — throws when id does not exist")
    void deleteById_WhenIdInvalid_ShouldThrowNotFound() {
        Long id = 999L;

        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteById(id));

        verify(categoryRepository).existsById(id);
    }
}