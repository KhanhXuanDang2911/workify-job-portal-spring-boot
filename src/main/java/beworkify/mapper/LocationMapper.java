package beworkify.mapper;

import beworkify.dto.response.LocationResponse;
import beworkify.entity.Location;
import java.util.Set;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between Location entity and DTOs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    uses = {DistrictMapper.class, ProvinceMapper.class})
public interface LocationMapper {
  LocationResponse toDTO(Location entity);

  Set<LocationResponse> toDTOs(Set<Location> locations);
}
