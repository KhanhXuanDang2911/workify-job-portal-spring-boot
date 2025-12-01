package beworkify.search.repository;

import beworkify.search.document.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for performing Elasticsearch operations on JobDocument.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, String> {}
