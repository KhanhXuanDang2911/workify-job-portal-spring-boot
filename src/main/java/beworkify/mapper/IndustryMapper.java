package beworkify.mapper;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.entity.Industry;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndustryMapper {
    @Mapping(target = "jobIndustries", ignore = true)
    Industry toEntity(IndustryRequest request);

    IndustryResponse toDTO(Industry entity);

    List<IndustryResponse> toDTOs(List<Industry> entity);

    @Mapping(target = "jobIndustries", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(IndustryRequest request, @MappingTarget Industry entity);
}
