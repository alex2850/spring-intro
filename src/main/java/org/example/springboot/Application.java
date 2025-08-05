package org.example.springboot;

import java.math.BigDecimal;
import org.example.springboot.model.Book;
import org.example.springboot.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Autowired
    private BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            Book book = new Book();
            book.setAuthor("Jack Bauer");
            book.setTitle("Spring Boot");
            book.setIsbn("978-3-16-148410-0");
            book.setCoverImage("test");
            book.setPrice(new BigDecimal(99));

            bookRepository.save(book);

            System.out.println(bookRepository.findAll());
        };
    }

}
