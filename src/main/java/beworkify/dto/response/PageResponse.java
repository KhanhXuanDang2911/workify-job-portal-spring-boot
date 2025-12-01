package beworkify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic DTO for paginated response. Contains page metadata and list of items.
 *
 * @param <T> Type of items in the page.
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
  private Integer pageNumber;
  private Integer pageSize;
  private Integer totalPages;
  private Integer numberOfElements;
  private T items;
}
