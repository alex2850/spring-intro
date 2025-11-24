package org.example.springboot.mapper;

import org.example.springboot.config.MapperConfig;
import org.example.springboot.dto.CategoryDto;
import org.example.springboot.dto.UpdateCategoryRequestDto;
import org.example.springboot.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryDto dto);

    void updateModel(UpdateCategoryRequestDto requestDto, @MappingTarget Category category);
}
