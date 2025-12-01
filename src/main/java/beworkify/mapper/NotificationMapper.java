package beworkify.mapper;

import beworkify.dto.response.NotificationResponse;
import beworkify.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Notification entity and DTOs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(target = "createdAt", source = "createdAt")
  NotificationResponse toDTO(Notification notification);
}
