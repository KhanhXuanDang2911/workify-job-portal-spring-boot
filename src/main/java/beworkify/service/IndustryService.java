
package beworkify.service;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Industry;
import java.util.List;

public interface IndustryService {
	IndustryResponse create(IndustryRequest request);

	IndustryResponse update(Long id, IndustryRequest request);

	void delete(Long id);

	IndustryResponse getById(Long id);

	PageResponse<List<IndustryResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize, List<String> sorts,
			String keyword, Long categoryId);

	List<IndustryResponse> getAll();

	Industry findIndustryById(Long id);
}
