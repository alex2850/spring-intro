package org.example.springboot.service;

import java.util.List;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);
}
