package org.example.springboot.repository;

import java.util.List;
import java.util.Optional;
import org.example.springboot.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findBookById(Long id);
}
