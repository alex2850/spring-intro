package org.example.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "test", roles = {"ADMIN"})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)   // отключаем Security
@TestPropertySource(properties = {
        "jwt.secret=awfEWF432r32r23r23RFSDF23423r23r32r23r23f23423RFAWER2342342",
        "jwt.expiration=450000"
})
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateBookRequestDto buildBook(
            String title, String author, String isbn, int price) {

        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setIsbn(isbn);
        dto.setPrice(BigDecimal.valueOf(price));
        dto.setDescription("Desc");
        dto.setCoverImage("img.png");
        return dto;
    }

    @Test
    @DisplayName("GET /books — returns all books")
    void getAllBooks_ShouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /books/{id} — returns book by id")
    void getBookById_ShouldReturnBook() throws Exception {
        CreateBookRequestDto book =
                buildBook("Test Title", "Test Author", "978-1-23456-789-9", 50);

        MvcResult created = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto createdBook =
                objectMapper.readValue(
                        created.getResponse().getContentAsByteArray(),
                        BookDto.class
                );

        Long id = createdBook.getId();

        MvcResult result = mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andReturn();

        BookDto response =
                objectMapper.readValue(
                        result.getResponse().getContentAsByteArray(),
                        BookDto.class
                );

        Assertions.assertEquals("Test Title", response.getTitle());
        Assertions.assertEquals("Test Author", response.getAuthor());
        Assertions.assertEquals(id, response.getId());
    }

    @Test
    @DisplayName("POST /books — creates a new book")
    void createBook_ShouldCreate() throws Exception {

        CreateBookRequestDto dto =
                buildBook("TestBook", "Tester", "978-1-11111-222-3", 50);

        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("TestBook");
    }
}
