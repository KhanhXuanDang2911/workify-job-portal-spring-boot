package beworkify.mapper;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.ProvinceResponse;
import beworkify.entity.Province;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
    Province toEntity(ProvinceRequest request);

    ProvinceResponse toDTO(Province entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProvinceRequest request, @MappingTarget Province entity);
}
