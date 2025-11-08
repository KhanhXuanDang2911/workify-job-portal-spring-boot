
package beworkify.service.impl;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.dto.response.EmployerSummaryResponse;
import beworkify.dto.response.JobSummaryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Application;
import beworkify.entity.Job;
import beworkify.entity.User;
import beworkify.enumeration.ApplicationStatus;
import beworkify.enumeration.JobStatus;
import beworkify.enumeration.UserRole;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.ApplicationMapper;
import beworkify.repository.ApplicationRepository;
import beworkify.service.ApplicationService;
import beworkify.service.AzureBlobService;
import beworkify.service.JobService;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

	private static final int MAX_APPLY_PER_JOB = 3;

	private final ApplicationRepository repository;
	private final ApplicationMapper mapper;
	private final JobService jobService;
	private final AzureBlobService azureBlobService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Override
	@Transactional
	public ApplicationResponse create(ApplicationRequest request, MultipartFile cv) {
		Job job = jobService.findJobById(request.getJobId());
		if (!job.getStatus().equals(JobStatus.APPROVED)) {
			String message = messageSource.getMessage("application.job.not.approved", null,
					LocaleContextHolder.getLocale());
			throw new ResourceConflictException(message);
		}
		Long userId = AppUtils.getUserIdFromSecurityContext();
		User user = userService.findUserById(userId);

		long appliedCount = repository.countByUserIdAndJobId(userId, job.getId());
		if (appliedCount >= MAX_APPLY_PER_JOB) {
			String message = messageSource.getMessage("application.limit.exceeded", null,
					LocaleContextHolder.getLocale());
			throw new ResourceConflictException(message);
		}

		Application entity = mapper.toEntity(request);
		entity.setUser(user);
		entity.setJob(job);
		entity.setApplyCount((int) appliedCount + 1);
		entity.setStatus(ApplicationStatus.UNREAD);
		String uploadedUrl = uploadCv(cv);
		entity.setCvUrl(uploadedUrl);
		Application saved = repository.save(entity);
		return mapper.toDTO(saved);
	}

	@Override
	public void deleteById(Long id) {
		existsById(id);
		repository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public ApplicationResponse getLatestByJob(Long jobId) {
		Long userId = AppUtils.getUserIdFromSecurityContext();
		Application latest = repository.findTopByUserIdAndJobIdOrderByCreatedAtDesc(userId, jobId).orElseThrow(() -> {
			String message = messageSource.getMessage("application.notFound", null, LocaleContextHolder.getLocale());
			return new ResourceNotFoundException(message);
		});
		ApplicationResponse response = mapper.toDTO(latest);
		return mapJobSummary(response, latest.getJob());
	}

	@Override
	@Transactional(readOnly = true)
	public ApplicationResponse getById(Long id) {
		Application application = findById(id);
		Job job = application.getJob();
		checkOwnershipWithEmployer(application);
		ApplicationResponse response = mapper.toDTO(application);
		return mapJobSummary(response, job);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<List<ApplicationResponse>> getMyApplications(int pageNumber, int pageSize, List<String> sorts) {
		Long userId = AppUtils.getUserIdFromSecurityContext();
		User user = userService.findUserById(userId);

		List<String> whitelist = List.of("createdAt", "updatedAt");
		Pageable pageable = AppUtils.generatePageableWithSort(sorts, whitelist, pageNumber, pageSize);
		Page<Application> page = repository.findAllByUser(user, pageable);
		List<ApplicationResponse> items = page.getContent().stream().map(a -> {
			ApplicationResponse response = mapper.toDTO(a);
			return mapJobSummary(response, a.getJob());
		}).toList();

		return PageResponse.<List<ApplicationResponse>>builder().pageNumber(pageNumber).pageSize(pageSize)
				.totalPages(page.getTotalPages()).numberOfElements(page.getNumberOfElements()).items(items).build();
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<List<ApplicationResponse>> getApplicationsByJobId(int pageNumber, int pageSize, Long jobId,
			Integer receivedWithin, ApplicationStatus status) {
		Long employerId = AppUtils.getEmployerIdFromSecurityContext();
		Job job = jobService.findJobById(jobId);
		if (!job.getAuthor().getId().equals(employerId)) {
			throw new AccessDeniedException("Access is denied");
		}
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		LocalDateTime thresholdDateTime = (receivedWithin != null)
				? LocalDateTime.now().minusDays(receivedWithin)
				: null;
		Page<Application> page = repository.findByJobId(jobId, status, thresholdDateTime, pageable);

		List<ApplicationResponse> items = page.getContent().stream().map(a -> {
			ApplicationResponse response = mapper.toDTO(a);
			return mapJobSummary(response, a.getJob());
		}).toList();

		return PageResponse.<List<ApplicationResponse>>builder().pageNumber(pageNumber).pageSize(pageSize)
				.totalPages(page.getTotalPages()).numberOfElements(page.getNumberOfElements()).items(items).build();
	}

	@Override
	@Transactional
	public ApplicationResponse createWithoutFile(ApplicationRequest request) {
		Job job = jobService.findJobById(request.getJobId());
		if (!job.getStatus().equals(JobStatus.APPROVED)) {
			String message = messageSource.getMessage("application.job.not.approved", null,
					LocaleContextHolder.getLocale());
			throw new ResourceConflictException(message);
		}
		Long userId = AppUtils.getUserIdFromSecurityContext();
		User user = userService.findUserById(userId);

		long appliedCount = repository.countByUserIdAndJobId(userId, job.getId());
		if (appliedCount >= MAX_APPLY_PER_JOB) {
			String message = messageSource.getMessage("application.limit.exceeded", null,
					LocaleContextHolder.getLocale());
			throw new ResourceConflictException(message);
		}

		if (appliedCount == 0) {
			String message = messageSource.getMessage("application.link.apply.require.previous", null,
					LocaleContextHolder.getLocale());
			throw new ResourceConflictException(message);
		}

		Application entity = mapper.toEntity(request);
		entity.setUser(user);
		entity.setJob(job);
		entity.setApplyCount((int) appliedCount + 1);
		entity.setStatus(ApplicationStatus.UNREAD);
		entity.setCvUrl(request.getCvUrl());
		Application saved = repository.save(entity);
		return mapper.toDTO(saved);
	}

	@Override
	@Transactional
	public ApplicationResponse changeStatus(Long id, ApplicationStatus status) {
		Application application = findById(id);
		checkOwnershipWithEmployer(application);
		application.setStatus(status);
		Application saved = repository.save(application);
		ApplicationResponse response = mapper.toDTO(saved);
		return mapJobSummary(response, saved.getJob());
	}

	private Application findById(Long id) {
		return repository.findById(id).orElseThrow(() -> {
			String message = messageSource.getMessage("application.notFound", null, LocaleContextHolder.getLocale());
			return new ResourceConflictException(message);
		});
	}

	private void checkOwnershipWithEmployer(Application application) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (AppUtils.hasRole(authentication, UserRole.ADMIN.getName())
				|| AppUtils.hasRole(authentication, UserRole.JOB_SEEKER.getName())) {
			Long userId = AppUtils.getUserIdFromSecurityContext();
			if (!application.getUser().getId().equals(userId)) {
				throw new AccessDeniedException("Access is denied");
			}
		} else {
			Long employerId = AppUtils.getEmployerIdFromSecurityContext();
			if (!application.getJob().getAuthor().getId().equals(employerId)) {
				throw new AccessDeniedException("Access is denied");
			}
		}
	}

	private String uploadCv(MultipartFile cv) {
		if (cv == null || cv.isEmpty()) {
			return null;
		}
		return azureBlobService.uploadFile(cv);
	}

	private ApplicationResponse mapJobSummary(ApplicationResponse response, Job job) {
		response.setJob(JobSummaryResponse.builder().jobTitle(job.getJobTitle())
				.employer(EmployerSummaryResponse.builder().email(job.getAuthor().getEmail())
						.employerSlug(job.getAuthor().getEmployerSlug()).avatarUrl(job.getAuthor().getAvatarUrl())
						.backgroundUrl(job.getAuthor().getBackgroundUrl()).companyName(job.getCompanyName()).build())
				.status(job.getStatus()).id(job.getId()).build());
		return response;
	}

	private void existsById(Long id) {
		if (!repository.existsById(id)) {
			String message = messageSource.getMessage("application.notFound", null, LocaleContextHolder.getLocale());
			throw new ResourceNotFoundException(message);
		}
	}
}
