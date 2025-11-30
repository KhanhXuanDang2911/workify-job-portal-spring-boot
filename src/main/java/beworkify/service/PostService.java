package beworkify.service;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PostResponse;
import java.util.List;

import jakarta.validation.constraints.Min;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
  PostResponse create(PostRequest request, MultipartFile thumbnail) throws Exception;

  PostResponse update(Long id, PostRequest request, MultipartFile thumbnail) throws Exception;

  void delete(Long id);

  PostResponse getById(Long id);

  PageResponse<List<PostResponse>> getAll(
      int pageNumber,
      int pageSize,
      List<String> sorts,
      String keyword,
      Long categoryId,
      boolean isPublic);

  PageResponse<List<PostResponse>> getMyPosts(
      int pageNumber,
      int pageSize,
      List<String> sorts,
      String keyword,
      Long categoryId,
      String status);

  List<PostResponse> getRelated(Long postId, int limit);

  List<PostResponse> getLatestPosts(int limit);

  PostResponse updateStatus(Long id, String status);
}
