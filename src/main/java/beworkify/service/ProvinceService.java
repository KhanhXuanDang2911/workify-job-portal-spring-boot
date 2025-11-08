
package beworkify.service;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ProvinceResponse;
import beworkify.entity.Province;
import java.util.List;

public interface ProvinceService {
	ProvinceResponse create(ProvinceRequest request);

	ProvinceResponse update(Long id, ProvinceRequest request);

	void delete(Long id);

	ProvinceResponse getById(Long id);

	PageResponse<List<ProvinceResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize, List<String> sorts,
			String keyword);

	Province findProvinceById(Long id);

	List<ProvinceResponse> getAll();
}
