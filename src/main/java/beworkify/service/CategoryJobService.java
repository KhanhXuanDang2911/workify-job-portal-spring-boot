package beworkify.service;

import beworkify.dto.request.CategoryJobRequest;
import beworkify.dto.response.CategoryJobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.CategoryJob;

import java.util.List;

public interface CategoryJobService {
    CategoryJobResponse create(CategoryJobRequest request);

    CategoryJobResponse update(Long id, CategoryJobRequest request);

    void delete(Long id);

    CategoryJobResponse getById(Long id);

    PageResponse<List<CategoryJobResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
                                                                        List<String> sorts, String keyword);

    List<CategoryJobResponse> getAll();

    CategoryJob findById(Long id);

    List<CategoryJobResponse> getCategoriesJobWithCountJobIndustry();
}
