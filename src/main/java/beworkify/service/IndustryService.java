package beworkify.service;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.PageResponse;

import java.util.List;

public interface IndustryService {
    IndustryResponse create(IndustryRequest request);

    IndustryResponse update(Long id, IndustryRequest request);

    void delete(Long id);

    IndustryResponse getById(Long id);

    PageResponse<List<IndustryResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword);

    List<IndustryResponse> getAll();
}
