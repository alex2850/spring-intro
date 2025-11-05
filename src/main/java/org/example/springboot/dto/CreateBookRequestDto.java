package org.example.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookRequestDto {
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;
    @NotBlank(message = "Author cannot be blank")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String author;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotBlank
    @Pattern(regexp = "^(?:97[89][\\s-]?)?\\d{1,5}[\\s-]?\\d{1,7}[\\s-]?\\d{1,7}[\\s-]?[\\dXx]$")
    private String isbn;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    private String coverImage;
}
