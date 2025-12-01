package beworkify.service;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;
import java.util.List;

/**
 * Service interface for managing saved jobs. Allows job seekers to save and manage their favorite
 * job listings.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface SavedJobService {

  void toggle(Long jobId);

  boolean isSaved(Long jobId);

  PageResponse<List<JobResponse>> getSavedJobs(int pageNumber, int pageSize);
}
