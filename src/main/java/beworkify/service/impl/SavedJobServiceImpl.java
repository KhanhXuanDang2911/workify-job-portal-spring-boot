
package beworkify.service.impl;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Job;
import beworkify.entity.SavedJob;
import beworkify.entity.User;
import beworkify.mapper.JobMapper;
import beworkify.repository.ApplicationRepository;
import beworkify.repository.JobRepository;
import beworkify.repository.SavedJobRepository;
import beworkify.service.JobService;
import beworkify.service.SavedJobService;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

	private final SavedJobRepository repository;
	private final JobRepository jobRepository;
	private final ApplicationRepository applicationRepository;
	private final JobService jobService;
	private final UserService userService;
	private final JobMapper mapper;

	@Override
	@Transactional
	public void toggle(Long jobId) {
		Long userId = AppUtils.getUserIdFromSecurityContext();
		log.info("Toggle saved job: userId={}, jobId={}", userId, jobId);

		User user = userService.findUserById(userId);
		Job job = jobService.findJobById(jobId);

		if (job.getStatus() != beworkify.enumeration.JobStatus.APPROVED) {
			throw new AccessDeniedException("Access is denied");
		}

		repository.findByUser_IdAndJob_Id(userId, jobId).ifPresentOrElse(existing -> {
			repository.delete(existing);
			log.info("Un-saved job {} for user {}", jobId, userId);
		}, () -> {
			SavedJob sj = SavedJob.builder().user(user).job(job).build();
			repository.save(sj);
			log.info("Saved job {} for user {}", jobId, userId);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<List<JobResponse>> getSavedJobs(int pageNumber, int pageSize) {
		Long userId = AppUtils.getUserIdFromSecurityContext();
		log.info("Fetching saved jobs for userId={}, pageNumber={}, pageSize={}", userId, pageNumber, pageSize);

		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Long> page = repository.findJobIdsByUserId(userId, pageable);

		List<Long> orderedIds = page.getContent();
		List<Job> fetchedJobs = orderedIds.isEmpty() ? List.of() : jobRepository.fetchJobsByIds(orderedIds);
		Map<Long, Job> jobById = fetchedJobs.stream().collect(Collectors.toMap(Job::getId, j -> j));
		List<Job> jobsOrdered = orderedIds.stream().map(jobById::get).filter(Objects::nonNull).toList();

		List<JobResponse> items = mapWithApplicationCounts(jobsOrdered, orderedIds);
		return toPageResponse(page, items);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isSaved(Long jobId) {
		Long userId = AppUtils.getUserIdFromSecurityContext();
		// Validate job exists (avoid silently returning false on invalid job)
		jobService.findJobById(jobId);
		boolean exists = repository.existsByUser_IdAndJob_Id(userId, jobId);
		log.debug("Check saved job: userId={}, jobId={}, exists={}", userId, jobId, exists);
		return exists;
	}

	private PageResponse<List<JobResponse>> toPageResponse(Page<Long> page, List<JobResponse> items) {
		return PageResponse.<List<JobResponse>>builder().pageNumber(page.getNumber() + 1).pageSize(page.getSize())
				.totalPages(page.getTotalPages()).numberOfElements(page.getNumberOfElements()).items(items).build();
	}

	private List<JobResponse> mapWithApplicationCounts(List<Job> jobsOrdered, List<Long> orderedIds) {
		List<JobResponse> items = mapper.toDTOs(jobsOrdered);
		if (orderedIds == null || orderedIds.isEmpty())
			return items;
		var rows = applicationRepository.countByJobIds(orderedIds);
		Map<Long, Long> countMap = rows.stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
		items.forEach(dto -> dto.setNumberOfApplications(countMap.getOrDefault(dto.getId(), 0L).intValue()));
		return items;
	}
}
