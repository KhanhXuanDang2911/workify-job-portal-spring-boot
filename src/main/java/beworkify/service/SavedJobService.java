package beworkify.service;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;

import java.util.List;

public interface SavedJobService {

    void toggle(Long jobId);

    boolean isSaved(Long jobId);

    PageResponse<List<JobResponse>> getSavedJobs(int pageNumber, int pageSize);
}
