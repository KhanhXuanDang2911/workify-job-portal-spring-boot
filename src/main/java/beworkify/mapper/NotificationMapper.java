
package beworkify.mapper;

import beworkify.dto.response.NotificationResponse;
import beworkify.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

	@Mapping(target = "createdAt", source = "createdAt")
	NotificationResponse toDTO(Notification notification);
}
