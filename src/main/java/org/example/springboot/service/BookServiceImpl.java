package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.BookDto;
import org.example.springboot.dto.CreateBookRequestDto;
import org.example.springboot.dto.UpdateBookRequestDto;
import org.example.springboot.exception.EntityNotFoundException;
import org.example.springboot.mapper.BookMapper;
import org.example.springboot.model.Book;
import org.example.springboot.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.toModel(bookRequestDto);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toBookDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findBookById(id).orElseThrow(
                () -> new EntityNotFoundException("Can not find book by id" + id)
        );
        return bookMapper.toBookDto(book);
    }
  
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, UpdateBookRequestDto bookRequestDto) {
        Book book = bookRepository.findBookById(id).orElseThrow(
                () -> new EntityNotFoundException("Can not find book by id " + id));

        bookMapper.updateBookFromDto(bookRequestDto, book);

        return bookMapper.toBookDto(bookRepository.save(book));
    }
}
