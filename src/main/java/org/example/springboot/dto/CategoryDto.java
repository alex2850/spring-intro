package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        @Schema(description = "Unique identifier of the category",
                example = "1")
        Long id,

        @Schema(description = "Name of the category",
                example = "Fiction")
        @NotBlank
        @Size(max = 255)
        String name,

        @Schema(description = "Description of the category",
                example = "Fiction books")
        @NotBlank
        @Size(max = 1000)
        String description
) {
}
