package beworkify.search.repository;

import beworkify.search.document.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, String> {
}
