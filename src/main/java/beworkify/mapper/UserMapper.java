package beworkify.mapper;

import beworkify.dto.request.UserRequest;
import beworkify.dto.response.UserResponse;
import beworkify.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { ProvinceMapper.class, DistrictMapper.class })
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "gender", ignore = true)
    User toEntity(UserRequest dto);

    @Mapping(target = "role", ignore = true)
    UserResponse toDTO(User entity);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserRequest request, @MappingTarget User user);

}
