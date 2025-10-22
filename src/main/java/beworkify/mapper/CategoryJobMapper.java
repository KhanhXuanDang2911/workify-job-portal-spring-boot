package beworkify.mapper;

import beworkify.dto.request.CategoryJobRequest;
import beworkify.dto.response.CategoryJobResponse;
import beworkify.entity.CategoryJob;
import org.mapstruct.*;

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
