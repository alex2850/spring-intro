package org.example.springboot.repository;

import java.util.List;
import org.example.springboot.model.Book;

public interface BookRepository {
    Book save(Book book);

    List findAll();
}
