package beworkify.mapper;

import beworkify.dto.response.LocationResponse;
import beworkify.entity.Location;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {DistrictMapper.class, ProvinceMapper.class})
public interface LocationMapper {
  LocationResponse toDTO(Location entity);

  Set<LocationResponse> toDTOs(Set<Location> locations);
}
