package org.example.springboot.service;

import java.util.List;
import org.example.springboot.model.Book;

interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
