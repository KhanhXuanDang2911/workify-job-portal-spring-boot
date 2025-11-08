
package beworkify.search.service;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Job;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface JobSearchService {
	void index(Job job);

	void deleteById(Long id);

	PageResponse<List<JobResponse>> searchAdvanced(String keyword, List<String> industryIds, List<String> provinceIds,
			List<String> jobLevels, List<String> jobTypes, List<String> experienceLevels, List<String> educationLevels,
			Integer postedWithinDays, Double minSalary, Double maxSalary, String salaryUnit, String sort,
			Pageable pageable);

	void indexAll(Iterable<Job> jobs);
}
