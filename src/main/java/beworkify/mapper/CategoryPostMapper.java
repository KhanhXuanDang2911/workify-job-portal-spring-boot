package beworkify.mapper;

import beworkify.dto.request.CategoryPostRequest;
import beworkify.dto.response.CategoryPostResponse;
import beworkify.entity.CategoryPost;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryPostMapper {
  @Mapping(target = "slug", ignore = true)
  CategoryPost toEntity(CategoryPostRequest request);

  CategoryPostResponse toDTO(CategoryPost entity);

  @Mapping(target = "slug", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(CategoryPostRequest request, @MappingTarget CategoryPost entity);
}
