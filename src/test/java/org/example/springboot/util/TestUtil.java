package org.example.springboot.util;

import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import java.math.BigDecimal;
import java.util.List;

public class TestUtil {

    public static CreateBookRequestDto createBookRequest() {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("Test Title");
        dto.setAuthor("Test Author");
        dto.setIsbn("978-" + System.nanoTime());
        dto.setPrice(BigDecimal.valueOf(9.99));
        dto.setDescription("Some description");
        return dto;
    }

    public static UpdateBookRequestDto updateBookRequest() {
        UpdateBookRequestDto dto = new UpdateBookRequestDto();
        dto.setTitle("Updated Title");
        dto.setAuthor("Updated Author");
        dto.setPrice(BigDecimal.valueOf(12.99));
        return dto;
    }

    public static List<BookDto> createListOfBookDto() {
        BookDto firstDto = new BookDto()
                .setId(1L)
                .setTitle("Test Book 1")
                .setAuthor("Test Author 1")
                .setIsbn("111-" + System.nanoTime())
                .setPrice(BigDecimal.valueOf(10.99));

        BookDto secondDto = new BookDto()
                .setId(2L)
                .setTitle("Test Book 2")
                .setAuthor("Test Author 2")
                .setIsbn("222-" + System.nanoTime())
                .setPrice(BigDecimal.valueOf(15.99));

        BookDto thirdDto = new BookDto()
                .setId(3L)
                .setTitle("Test Book 3")
                .setAuthor("Test Author 3")
                .setIsbn("333-" + System.nanoTime())
                .setPrice(BigDecimal.valueOf(20.99));

        return List.of(firstDto, secondDto, thirdDto);
    }

    public static List<CreateBookRequestDto> createListOfBookRequestDto() {
        CreateBookRequestDto firstDto = new CreateBookRequestDto()
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setPrice(BigDecimal.valueOf(11.99))
                .setIsbn("111-" + System.nanoTime())
                .setDescription("desc")
                .setCategoryIds(List.of(1L));

        CreateBookRequestDto secondDto = new CreateBookRequestDto()
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setPrice(BigDecimal.valueOf(12.99))
                .setIsbn("222-" + System.nanoTime())
                .setDescription("desc")
                .setCategoryIds(List.of(1L));

        CreateBookRequestDto thirdDto = new CreateBookRequestDto()
                .setTitle("Book 3")
                .setAuthor("Author 3")
                .setPrice(BigDecimal.valueOf(13.99))
                .setIsbn("222-" + System.nanoTime())
                .setDescription("desc")
                .setCategoryIds(List.of(1L));

        return List.of(firstDto, secondDto, thirdDto);
    }

}