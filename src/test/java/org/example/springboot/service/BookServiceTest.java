package org.example.springboot.service;

import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import org.example.springboot.mapper.BookMapperImpl;
import org.example.springboot.model.Book;
import org.example.springboot.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapperImpl bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("getBookById() — should return BookDto if exists")
    public void getBookById_ShouldReturnBookDto() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Kobzar");
        book.setDescription("Originally the title of the first collection of poems" +
                " by Taras Shevchenko");
        book.setPrice(new BigDecimal(1350));

        BookDto expected = new BookDto();
        expected.setId(bookId);
        expected.setTitle("Kobzar");
        expected.setDescription("Originally the title of the first collection of poems" +
                " by Taras Shevchenko");
        expected.setPrice(new BigDecimal(1350));

        Mockito.when(bookRepository.findBookById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.getBookById(bookId);
        Assertions.assertEquals("Kobzar", actual.getTitle());
        Assertions.assertEquals(bookId, actual.getId());

    }

    @Test
    @DisplayName("save() — should save book and return BookDto")
    public void save_ShouldReturnValidBookDto() {
        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("Kobzar");
        request.setDescription("Desc");
        request.setPrice(BigDecimal.TEN);

        Book book = new Book();
        book.setTitle("Kobzar");
        book.setDescription("Desc");
        book.setPrice(BigDecimal.TEN);

        Book saved = new Book();
        saved.setId(1L);
        saved.setTitle("Kobzar");
        saved.setDescription("Desc");
        saved.setPrice(BigDecimal.TEN);

        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Kobzar");
        dto.setDescription("Desc");
        dto.setPrice(BigDecimal.TEN);

        Mockito.when(bookMapper.toModel(request)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(saved);
        Mockito.when(bookMapper.toBookDto(saved)).thenReturn(dto);

        BookDto actual = bookService.save(request);

        Assertions.assertEquals(1L, actual.getId());
        Assertions.assertEquals("Kobzar", actual.getTitle());
    }

    @Test
    @DisplayName("findAll() — should return page of BookDto")
    public void findAll_ShouldReturnPagedBooks() {
        Pageable pageable = PageRequest.of(0, 2);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("A");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("B");

        Page<Book> page = new PageImpl<>(List.of(book1, book2), pageable, 2);

        BookDto dto1 = new BookDto();
        dto1.setId(1L);
        dto1.setTitle("A");

        BookDto dto2 = new BookDto();
        dto2.setId(2L);
        dto2.setTitle("B");

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(bookMapper.toBookDto(book1)).thenReturn(dto1);
        Mockito.when(bookMapper.toBookDto(book2)).thenReturn(dto2);

        Page<BookDto> result = bookService.findAll(pageable);

        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals("A", result.getContent().get(0).getTitle());
        Assertions.assertEquals("B", result.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("update() — should update and return updated BookDto")
    public void update_ShouldReturnUpdatedBookDto() {
        Long id = 1L;

        UpdateBookRequestDto request = new UpdateBookRequestDto();
        request.setTitle("Updated");
        request.setDescription("NewD");
        request.setPrice(BigDecimal.valueOf(200));

        Book existing = new Book();
        existing.setId(id);
        existing.setTitle("Old");

        Book saved = new Book();
        saved.setId(id);
        saved.setTitle("Updated");

        BookDto dto = new BookDto();
        dto.setId(id);
        dto.setTitle("Updated");

        Mockito.when(bookRepository.findBookById(id)).thenReturn(Optional.of(existing));
        Mockito.doAnswer(invocation -> {
            UpdateBookRequestDto req = invocation.getArgument(0);
            Book b = invocation.getArgument(1);
            b.setTitle(req.getTitle());
            b.setDescription(req.getDescription());
            b.setPrice(req.getPrice());
            return null;
        }).when(bookMapper).updateBookFromDto(request, existing);

        Mockito.when(bookRepository.save(existing)).thenReturn(saved);
        Mockito.when(bookMapper.toBookDto(saved)).thenReturn(dto);

        BookDto actual = bookService.update(id, request);

        Assertions.assertEquals("Updated", actual.getTitle());
        Assertions.assertEquals(id, actual.getId());
    }
}
