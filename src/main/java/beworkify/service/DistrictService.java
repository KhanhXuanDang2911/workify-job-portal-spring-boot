package beworkify.service;

import beworkify.dto.request.DistrictRequest;
import beworkify.dto.response.DistrictResponse;
import beworkify.entity.District;
import java.util.List;

/**
 * Service interface for managing districts. Provides business logic for district data operations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface DistrictService {
  DistrictResponse create(DistrictRequest request);

  DistrictResponse update(Long id, DistrictRequest request);

  void delete(Long id);

  DistrictResponse getById(Long id);

  District findDistrictById(Long id);

  List<DistrictResponse> getAll();

  List<DistrictResponse> getByProvinceId(Long provinceId);
}
