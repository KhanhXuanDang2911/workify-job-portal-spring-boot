
package beworkify.service;

import beworkify.dto.request.JobRequest;
import beworkify.dto.response.*;
import beworkify.entity.Job;
import beworkify.enumeration.JobStatus;
import java.util.List;

public interface JobService {
	JobResponse create(JobRequest request);

	JobResponse update(Long id, JobRequest request);

	void delete(Long id);

	Job findJobById(Long id);

	JobResponse getById(Long id);

	PageResponse<List<JobResponse>> getMyJobs(int pageNumber, int pageSize, Long industryId, Long provinceId,
			List<String> sorts, String keyword);

	PageResponse<List<JobResponse>> getAllJobs(int pageNumber, int pageSize, Long industryId, Long provinceId,
			List<String> sorts, String keyword);

	List<IndustryResponse> getMyCurrentIndustries(Long employerId);

	List<ProvinceResponse> getMyCurrentLocations(Long employerId);

	List<PopularLocationResponse> getPopularLocations(Integer limit);

	List<PopularIndustryResponse> getPopularIndustries(Integer limit);

	void closeJob(Long id);

	void updateStatus(Long id, JobStatus jobStatus);

	PageResponse<List<JobResponse>> getHiringJobs(Long employerId, int pageNumber, int pageSize, List<String> sorts);

	List<JobResponse> getTopAttractiveJobs(Integer limit);
}
