package org.example.springboot.repository;

import java.util.Optional;
import org.example.springboot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBookById(Long id);
}
