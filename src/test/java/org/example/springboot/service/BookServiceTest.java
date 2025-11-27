package org.example.springboot.service;

import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import org.example.springboot.mapper.BookMapperImpl;
import org.example.springboot.model.Book;
import org.example.springboot.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void getBookById_ShouldReturnBookDto_OK() {
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

        when(bookRepository.findBookById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.getBookById(bookId);
        assertEquals(expected, actual);

        verify(bookRepository).findBookById(bookId);
        verify(bookMapper).toBookDto(book);
    }

    @Test
    @DisplayName("save() — should save book and return BookDto")
    public void save_WithValidRequestDto_Ok() {
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

        when(bookMapper.toModel(request)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(saved);
        when(bookMapper.toBookDto(saved)).thenReturn(dto);

        BookDto actual = bookService.save(request);

        assertEquals(dto, actual);

        verify(bookMapper).toModel(request);
        verify(bookRepository).save(book);
        verify(bookMapper).toBookDto(saved);
    }

    @Test
    @DisplayName("findAll() — should return page of BookDto")
    public void findAll_ShouldReturnPagedBooks() {
        Pageable pageable = PageRequest.of(0, 2);

        Book bookKobzar = new Book();
        bookKobzar.setId(1L);
        bookKobzar.setTitle("A");

        Book bookTale = new Book();
        bookTale.setId(2L);
        bookTale.setTitle("B");

        Page<Book> page = new PageImpl<>(List.of(bookKobzar, bookTale), pageable, 2);

        BookDto dtoKobzar = new BookDto();
        dtoKobzar.setId(1L);
        dtoKobzar.setTitle("A");

        BookDto dtoTale = new BookDto();
        dtoTale.setId(2L);
        dtoTale.setTitle("B");

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toBookDto(bookKobzar)).thenReturn(dtoKobzar);
        when(bookMapper.toBookDto(bookTale)).thenReturn(dtoTale);

        List<BookDto> result = bookService.findAll(pageable).toList();

        assertEquals(2, result.size());
        assertEquals(List.of(dtoKobzar, dtoTale), result);

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toBookDto(bookKobzar);
        verify(bookMapper).toBookDto(bookTale);
    }

    @Test
    @DisplayName("update() — should update and return updated BookDto")
    public void update_WithValidRequestDto_Ok() {
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

        when(bookRepository.findBookById(id)).thenReturn(Optional.of(existing));

        when(bookRepository.save(existing)).thenReturn(saved);

        when(bookMapper.toBookDto(saved)).thenReturn(dto);

        BookDto actual = bookService.update(id, request);

        assertEquals(dto, actual);

        verify(bookRepository).findBookById(id);
        verify(bookRepository).save(existing);
        verify(bookMapper).toBookDto(saved);
    }

    @Test
    @DisplayName("update() — should throw when book does not exist")
    public void update_WithInvalidRequestDto_ShouldThrow() {
        Long id = 999L;

        UpdateBookRequestDto request = new UpdateBookRequestDto();
        request.setTitle("Updated");

        when(bookRepository.findBookById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.update(id, request));

        verify(bookRepository).findBookById(id);
    }
}
