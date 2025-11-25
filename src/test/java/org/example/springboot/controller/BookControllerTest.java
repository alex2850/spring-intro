package org.example.springboot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.repository.BookRepository;
import org.example.springboot.util.TestUtil;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "test", roles = {"ADMIN"})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
    }

    private BookDto createBook(CreateBookRequestDto dto) throws Exception {
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class
        );
    }

    @Test
    @DisplayName("GET /books — returns all books")
    void getAllBooks_ShouldReturnList_Ok() throws Exception {
        BookDto firstBook = createBook(TestUtil.createBookRequest());
        BookDto secondBook = createBook(TestUtil.createBookRequest());

        List<BookDto> expected = List.of(firstBook, secondBook);

        MvcResult response = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(response.getResponse().getContentAsByteArray());

        List<BookDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {}
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("GET /books/{id} — returns book by id")
    void getBookById_ShouldReturnBook_Ok() throws Exception {
        BookDto created = createBook(TestUtil.createBookRequest());

        MvcResult result = mockMvc.perform(get("/books/" + created.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class
        );

        assertEquals(created, actual);
    }

    @Test
    @DisplayName("POST /books — creates new book")
    void createBook_WithValidRequestDto_Ok() throws Exception {
        CreateBookRequestDto dto = TestUtil.createBookRequest();

        BookDto created = createBook(dto);

        assertEquals(dto.getTitle(), created.getTitle());
        assertEquals(dto.getAuthor(), created.getAuthor());
        assertEquals(dto.getIsbn(), created.getIsbn());
        assertEquals(dto.getPrice(), created.getPrice());
    }

    @Test
    @DisplayName("POST /books — should return 400 when title is blank")
    void createBook_WithInvalidTitle_NotOk() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto()
                .setTitle("")
                .setAuthor("Author")
                .setIsbn("1234567890123")
                .setPrice(BigDecimal.TEN)
                .setDescription("Some desc")
                .setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /books — should return 400 when price is null")
    void createBook_WhenPriceIsNull_NotOk() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto()
                .setTitle("Valid")
                .setAuthor("A")
                .setIsbn("1234567890123")
                .setPrice(null)
                .setDescription("Some desc")
                .setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /books — should return 400 when price is negative")
    void createBook_WhenPriceIsNegative_NotOk() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto()
                .setTitle("Valid")
                .setAuthor("A")
                .setIsbn("1234567890123")
                .setPrice(BigDecimal.valueOf(-10))
                .setDescription("Some desc")
                .setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /books — invalid request returns 400")
    void createBook_WithFullyInvalidDto_BadRequest() throws Exception {

        CreateBookRequestDto dto = new CreateBookRequestDto()
                .setTitle("")
                .setAuthor("")
                .setIsbn("")
                .setPrice(BigDecimal.valueOf(-10))
                .setDescription("")
                .setCoverImage("")
                .setCategoryIds(List.of());

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
