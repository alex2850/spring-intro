package org.example.springboot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.repository.BookRepository;
import org.example.springboot.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.springboot.util.TestUtil.createListOfBookDto;
import static org.example.springboot.util.TestUtil.createListOfBookRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("getAllBooks() — returns all books")
    void getAllBooks_ShouldReturnStatusOk() throws Exception {
        List<CreateBookRequestDto> requestBooks = createListOfBookRequestDto();

        List<BookDto> expected = new ArrayList<>();

        for (CreateBookRequestDto dto : requestBooks) {
            MvcResult created = mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            expected.add(objectMapper.readValue(
                    created.getResponse().getContentAsByteArray(),
                    BookDto.class
            ));
        }

        MvcResult response = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(response.getResponse().getContentAsByteArray());
        List<BookDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {}
        );

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("getBookById() — returns book by id")
    void getBookById_ShouldReturnBook() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequest();

        MvcResult created = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto createdBook = objectMapper.readValue(
                created.getResponse().getContentAsByteArray(),
                BookDto.class
        );

        Long id = createdBook.getId();

        MvcResult result = mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class
        );

        BookDto expected = new BookDto();
        expected.setId(id);
        expected.setTitle("Test Title");
        expected.setAuthor("Test Author");
        expected.setIsbn(request.getIsbn());
        expected.setPrice(BigDecimal.valueOf(9.99));
        expected.setDescription("Some description");
        expected.setCoverImage(null);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("createBook() — creates a new book")
    void createBook_ShouldCreate() throws Exception {

        CreateBookRequestDto dto =  TestUtil.createBookRequest();

        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Test Title");
    }

    @Test
    @DisplayName("createBook() — should return 400 when title is blank")
    public void createBook_ShouldFail_WhenTitleBlank() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("");
        dto.setDescription("Valid");
        dto.setPrice(BigDecimal.TEN);
        dto.setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("createBook() — should return 400 when price is null")
    public void createBook_ShouldFail_WhenPriceNull() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("ValidTitle");
        dto.setDescription("Valid");
        dto.setPrice(null);
        dto.setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("createBook() — should return 400 when price is negative")
    public void createBook_ShouldFail_WhenPriceNegative() throws Exception {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("Valid");
        dto.setDescription("Valid");
        dto.setPrice(BigDecimal.valueOf(-10));
        dto.setCategoryIds(List.of(1L));

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create book with invalid data")
    void save_WithInvalidRequestDto_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto()
                .setTitle("")
                .setAuthor("")
                .setIsbn("")
                .setPrice(BigDecimal.valueOf(-10))
                .setDescription("")
                .setCoverImage("")
                .setCategoryIds(List.of());
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);
        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}


