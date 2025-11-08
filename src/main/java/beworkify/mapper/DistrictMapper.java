
package beworkify.mapper;

import beworkify.dto.request.DistrictRequest;
import beworkify.dto.response.DistrictResponse;
import beworkify.entity.District;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DistrictMapper {
	District toEntity(DistrictRequest request);

	DistrictResponse toDTO(District entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(DistrictRequest request, @MappingTarget District entity);
}
