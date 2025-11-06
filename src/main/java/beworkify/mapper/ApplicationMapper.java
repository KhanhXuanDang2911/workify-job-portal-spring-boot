package beworkify.mapper;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.entity.Application;
import beworkify.entity.Job;
import beworkify.entity.User;
import beworkify.service.JobService;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {AppUtils.class})
public interface ApplicationMapper {

    ApplicationResponse toDTO(Application application);

    Application toEntity(ApplicationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ApplicationRequest request, @MappingTarget Application application);

}
