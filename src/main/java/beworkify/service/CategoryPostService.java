package beworkify.service;

import beworkify.dto.request.CategoryPostRequest;
import beworkify.dto.response.CategoryPostResponse;
import beworkify.dto.response.PageResponse;

import java.util.List;

public interface CategoryPostService {
    CategoryPostResponse create(CategoryPostRequest request);

    CategoryPostResponse update(Long id, CategoryPostRequest request);

    void delete(Long id);

    CategoryPostResponse getById(Long id);

    PageResponse<List<CategoryPostResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword);

    List<CategoryPostResponse> getAll();
}
