
package beworkify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
