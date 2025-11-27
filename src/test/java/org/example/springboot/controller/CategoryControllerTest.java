package org.example.springboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.example.springboot.model.Category;
import org.example.springboot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "test", roles = {"ADMIN"})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    @DisplayName("GET /categories — returns paged categories")
    void getAllCategory_ShouldReturnList_Ok() throws Exception {
        Category firstEntity = createCategory("Name1", "Desc1");
        Category secondEntity = createCategory("Name2", "Desc2");

        CategoryDto expected1 = new CategoryDto(firstEntity.getId(), "Name1", "Desc1");
        CategoryDto expected2 = new CategoryDto(secondEntity.getId(), "Name2", "Desc2");

        List<CategoryDto> expectedList = List.of(expected1, expected2);

        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsByteArray());

        List<CategoryDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {}
        );

        assertEquals(expectedList, actual);
    }

    @WithMockUser(username = "test", roles = {"USER"})
    @Test
    @DisplayName("GET /categories/{id} — returns category by id")
    void getById_ShouldReturnCategory_Ok() throws Exception {
        Category entity = createCategory("Science", "Desc");

        CategoryDto expected = new CategoryDto(entity.getId(), "Science", "Desc");

        MvcResult result = mockMvc.perform(get("/categories/" + entity.getId()))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class
        );

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("POST /categories — creates category")
    void createCategory_WithValidRequestDto_Ok() throws Exception {
        CategoryDto request = new CategoryDto(null, "Food", "Food products");

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);

        CategoryDto expected = new CategoryDto(actual.id(), "Food", "Food products");

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("PUT /categories/{id} — update existing category")
    void updateCategory_WithValidRequestDto_Ok() throws Exception {
        Category original = createCategory("OldName", "OldDesc");

        Long id = original.getId();

        UpdateCategoryRequestDto request = new UpdateCategoryRequestDto("Updated","New description");

        MvcResult result = mockMvc.perform(put("/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto.class
        );

        CategoryDto expected = new CategoryDto(id, "Updated", "New description");

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    @DisplayName("DELETE /categories/{id} — should delete category")
    void delete_ShouldReturnNoContent() throws Exception {

        Category entity = createCategory("DeleteName", "DeleteDesc");
        Long id = entity.getId();

        mockMvc.perform(delete("/categories/" + id))
                .andExpect(status().isNoContent());

        boolean exists = categoryRepository.existsById(id);

        assertFalse(exists, "Category should be deleted from DB");
    }

    @WithMockUser(username = "test", roles = {"USER"})
    @Test
    @DisplayName("GET /categories/{id} — invalid id returns 404")
    void getById_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        MvcResult result = mockMvc.perform(get("/categories/" + invalidId))
                .andExpect(status().isNotFound())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertEquals("Entity not found exception occurred", body);
    }

    @WithMockUser(username = "test", roles = {"ADMIN"})
    @Test
    @DisplayName("PUT /categories/{id} — invalid id returns 404")
    void update_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        UpdateCategoryRequestDto request =
                new UpdateCategoryRequestDto("New", "Desc");

        MvcResult result = mockMvc.perform(put("/categories/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertEquals("Entity not found exception occurred", body);
    }

    private Category createCategory(String name, String desc) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(desc);
        return categoryRepository.save(c);
    }
}