package org.example.springboot.service;

import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto update(Long id, UpdateBookRequestDto bookRequestDto);

    void deleteById(Long id);
}
