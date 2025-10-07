package beworkify.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse<T> {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
    private Integer numberOfElements;
    private T items;
}