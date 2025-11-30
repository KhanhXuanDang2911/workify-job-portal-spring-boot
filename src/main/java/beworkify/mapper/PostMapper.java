package beworkify.mapper;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PostResponse;
import beworkify.entity.Post;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {CategoryPostMapper.class})
public interface PostMapper {
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "readingTimeMinutes", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "employerAuthor", ignore = true)
  @Mapping(target = "userAuthor", ignore = true)
  Post toEntity(PostRequest request);

  @Mapping(target = "employerAuthor", ignore = true)
  @Mapping(target = "userAuthor", ignore = true)
  PostResponse toDTO(Post entity);

  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "readingTimeMinutes", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "employerAuthor", ignore = true)
  @Mapping(target = "userAuthor", ignore = true)
  @Mapping(target = "status", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(PostRequest request, @MappingTarget Post entity);
}
