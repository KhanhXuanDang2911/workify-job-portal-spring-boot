package beworkify.mapper;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.ProvinceResponse;
import beworkify.entity.Province;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for converting between Province entity and DTOs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ProvinceMapper {
  Province toEntity(ProvinceRequest request);

  ProvinceResponse toDTO(Province entity);

  List<ProvinceResponse> toDTOs(List<Province> entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(ProvinceRequest request, @MappingTarget Province entity);
}
