package beworkify.mapper;

import beworkify.dto.request.CategoryJobRequest;
import beworkify.dto.response.CategoryJobResponse;
import beworkify.entity.CategoryJob;
import org.mapstruct.*;

/**
 * Mapper for converting between CategoryJob entity and DTOs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface CategoryJobMapper {
  @Mapping(target = "industries", ignore = true)
  CategoryJob toEntity(CategoryJobRequest request);

  @Mapping(target = "industries", ignore = true)
  CategoryJobResponse toDTO(CategoryJob entity);

  @Mapping(target = "industries", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(CategoryJobRequest request, @MappingTarget CategoryJob entity);
}
