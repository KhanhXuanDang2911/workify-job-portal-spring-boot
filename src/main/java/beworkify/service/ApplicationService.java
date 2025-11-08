
package beworkify.service;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.dto.response.PageResponse;
import beworkify.enumeration.ApplicationStatus;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationService {
	ApplicationResponse create(ApplicationRequest request, MultipartFile cv);

	ApplicationResponse getById(Long id);

	ApplicationResponse getLatestByJob(Long jobId);

	void deleteById(Long id);

	PageResponse<List<ApplicationResponse>> getMyApplications(int pageNumber, int pageSize, List<String> sorts);

	PageResponse<List<ApplicationResponse>> getApplicationsByJobId(int pageNumber, int pageSize, Long jobId,
			Integer receivedWithin, ApplicationStatus status);

	ApplicationResponse createWithoutFile(ApplicationRequest request);

	ApplicationResponse changeStatus(Long id, ApplicationStatus status);
}
