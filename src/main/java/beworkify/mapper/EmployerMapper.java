package beworkify.mapper;

import beworkify.dto.request.EmployerRequest;
import beworkify.dto.response.EmployerResponse;
import beworkify.entity.Employer;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {ProvinceMapper.class, DistrictMapper.class})
public interface EmployerMapper {

  EmployerResponse toDTO(Employer entity);

  @Mapping(target = "companySize", ignore = true)
  @Mapping(target = "status", ignore = true)
  Employer toEntity(EmployerRequest dto);

  @Mapping(target = "companySize", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "password", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(EmployerRequest request, @MappingTarget Employer employer);
}
