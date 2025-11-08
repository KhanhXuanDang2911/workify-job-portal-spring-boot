
package beworkify.mapper;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PostResponse;
import beworkify.entity.Post;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryPostMapper.class})
public interface PostMapper {
	@Mapping(target = "slug", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "readingTimeMinutes", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "author", ignore = true)
	Post toEntity(PostRequest request);

	@Mapping(target = "author", ignore = true)
	PostResponse toDTO(Post entity);

	@Mapping(target = "slug", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "readingTimeMinutes", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "author", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(PostRequest request, @MappingTarget Post entity);
}
