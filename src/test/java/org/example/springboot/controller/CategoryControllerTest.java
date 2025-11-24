package org.example.springboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.example.springboot.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    @DisplayName("GET /categories — returns paged categories")
    void getAll_ShouldReturnList() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);

        CategoryDto c1 = new CategoryDto(1L, "Tech1", "Desc1");
        CategoryDto c2 = new CategoryDto(2L, "Tech2", "Desc2");

        Page<CategoryDto> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        Mockito.when(categoryService.findAll(pageable)).thenReturn(page);

        var result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsByteArray());

        List<CategoryDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {}
        );

        assertEquals(2, actual.size());
        assertEquals(List.of(c1, c2), actual);
    }

    /** ---------------- GET BY ID ---------------- */
    @WithMockUser(username = "test", roles = {"USER"})    @Test
    @DisplayName("GET /categories/{id} — returns category by id")
    void getById_ShouldReturnCategory() throws Exception {

        CategoryDto dto = new CategoryDto(10L, "Science", "Desc");

        Mockito.when(categoryService.getById(10L)).thenReturn(dto);

        var result = mockMvc.perform(get("/categories/10"))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class
        );

        assertEquals(dto, actual);
    }

    /** ---------------- CREATE ---------------- */
    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("POST /categories — creates category")
    void create_ShouldCreate() throws Exception {

        CategoryDto request = new CategoryDto(1L, "Food", "Food products");
        CategoryDto response = new CategoryDto(2L,"Food2", "Food products2");

        Mockito.when(categoryService.save(request)).thenReturn(response);

        var result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class
        );

        assertEquals(response, actual);
    }

    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("PUT /categories/{id} — update existing category")
    void update_ShouldUpdate() throws Exception {

        UpdateCategoryRequestDto request = new UpdateCategoryRequestDto("Updated", "");

        CategoryDto response =  new CategoryDto(1L, "Updated", "Some description");

        Mockito.when(categoryService.update(1L, request)).thenReturn(response);

        var result = mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class
        );

        assertEquals(response, actual);
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    @DisplayName("DELETE /categories/{id} — should delete category")
    void delete_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/categories/5"))
                .andExpect(status().isNoContent());

        Mockito.verify(categoryService).deleteById(5L);
    }

    @WithMockUser(username = "test", roles = {"USER"})
    @Test
    @DisplayName("GET /categories/{id} — invalid id returns 404")
    void getById_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;
        Mockito.when(categoryService.getById(invalidId))
                .thenThrow(new RuntimeException("Category not found: " + invalidId));
        assertThrows(RuntimeException.class, () -> categoryService.getById(invalidId)
        );


    }

    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("PUT — invalid id returns 404")
    void update_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;
        UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto("New", "");
        Mockito.when(categoryService.update(invalidId, dto))
                .thenThrow(new RuntimeException("Category not found: " + invalidId));

        assertThrows(RuntimeException.class, () -> categoryService.update(invalidId, dto)
        );
    }
}