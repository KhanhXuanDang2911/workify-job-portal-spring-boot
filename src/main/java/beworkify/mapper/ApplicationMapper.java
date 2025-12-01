package beworkify.mapper;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.entity.Application;
import beworkify.util.AppUtils;
import org.mapstruct.*;

/**
 * Mapper for converting between Application entity and DTOs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    imports = {AppUtils.class})
public interface ApplicationMapper {

  ApplicationResponse toDTO(Application application);

  Application toEntity(ApplicationRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(ApplicationRequest request, @MappingTarget Application application);
}
