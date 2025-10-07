package beworkify.service;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest request, MultipartFile thumbnail) throws Exception;

    PostResponse update(Long id, PostRequest request, MultipartFile thumbnail) throws Exception;

    void delete(Long id);

    PostResponse getById(Long id);

    PageResponse<List<PostResponse>> getAll(int pageNumber, int pageSize, List<String> sorts, String keyword,
            Long categoryId, boolean isPublic, Long authorId);

    List<PostResponse> getRelated(Long postId, int limit);

    List<PostResponse> getLatestPosts(int limit);
}