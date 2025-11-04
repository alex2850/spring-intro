package org.example.springboot.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequestDto {
    private String title;
    private String author;
    @Positive
    private BigDecimal price;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    private String coverImage;
}
