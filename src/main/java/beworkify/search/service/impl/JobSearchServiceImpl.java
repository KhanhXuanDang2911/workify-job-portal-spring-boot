package beworkify.search.service.impl;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Job;
import beworkify.entity.Location;
import beworkify.enumeration.JobStatus;
import beworkify.mapper.JobMapper;
import beworkify.repository.JobRepository;
import beworkify.search.document.JobDocument;
import beworkify.search.repository.JobSearchRepository;
import beworkify.search.service.JobSearchService;
import beworkify.util.HtmlImageProcessor;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import co.elastic.clients.elasticsearch._types.FieldValue;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSearchServiceImpl implements JobSearchService {

    private final JobSearchRepository repository;
    private final ElasticsearchOperations operations;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public void index(Job job) {
        JobDocument doc = toDocument(job);
        repository.save(doc);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(String.valueOf(id));
    }

    @Override
    public void indexAll(Iterable<Job> jobs) {
        List<JobDocument> docs = new ArrayList<>();
        for (Job j : jobs) {
            docs.add(toDocument(j));
        }
        repository.saveAll(docs);
    }


    private String normalizeKeyword(String keyword) {
        if (keyword == null)
            return "";
        return keyword.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[^\\p{L}\\p{N}\\s]", "")
                .toLowerCase();
    }

    private JobDocument toDocument(Job job) {
        List<Long> industryIds = job.getJobIndustries().stream()
                .map(ji -> ji.getIndustry().getId())
                .collect(Collectors.toList());
        List<Long> provinceIds = job.getJobLocations().stream()
                .map(Location::getProvince)
                .map(p -> p.getId())
                .collect(Collectors.toList());

        JobDocument doc = JobDocument.builder()
                .id(String.valueOf(job.getId()))
                .jobTitle(job.getJobTitle())
                .companyName(job.getCompanyName())
                .requirement(HtmlImageProcessor.extractText(job.getRequirement()))
                .jobDescription(HtmlImageProcessor.extractText(job.getJobDescription()))
                .industries(industryIds)
                .provinces(provinceIds)
                .expirationDate(job.getExpirationDate())
                .status(job.getStatus() != null ? job.getStatus().name() : null)
                .createdAt(job.getCreatedAt() != null ? job.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
                .updatedAt(job.getUpdatedAt() != null ? job.getUpdatedAt().atOffset(ZoneOffset.UTC) : null)
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .salaryType(job.getSalaryType() != null ? job.getSalaryType().name() : null)
                .salaryUnit(job.getSalaryUnit() != null ? job.getSalaryUnit().name() : null)
                .jobLevel(job.getJobLevel() != null ? job.getJobLevel().name() : null)
                .jobType(job.getJobType() != null ? job.getJobType().name() : null)
                .experienceLevel(job.getExperienceLevel() != null ? job.getExperienceLevel().name() : null)
                .educationLevel(job.getEducationLevel() != null ? job.getEducationLevel().name() : null)
                .build();

        List<String> inputs = new ArrayList<>();
        if (job.getJobTitle() != null)
            inputs.add(job.getJobTitle());
        if (job.getCompanyName() != null)
            inputs.add(job.getCompanyName());
        doc.setSuggest(
                new Completion(inputs.toArray(String[]::new)));
        return doc;
    }

    @Override
    public PageResponse<List<JobResponse>> searchAdvanced(
            String keyword,
            List<String> industryIds,
            List<String> provinceIds,
            List<String> jobLevels,
            List<String> jobTypes,
            List<String> experienceLevels,
            List<String> educationLevels,
            Integer postedWithinDays,
            Double minSalary,
            Double maxSalary,
            String salaryUnit,
            String sort,
            Pageable pageable) {

        List<Long> industryIdList = industryIds == null ? List.of()
                : industryIds.stream()
                        .filter(StringUtils::hasText)
                        .map(Long::valueOf)
                        .filter(id -> id > 0)
                        .toList();

        List<Long> provinceIdList = provinceIds == null ? List.of()
                : provinceIds.stream()
                        .filter(StringUtils::hasText)
                        .map(Long::valueOf)
                        .filter(id -> id > 0)
                        .toList();

        var b = NativeQuery.builder();
        b.withQuery(q -> q.bool(bool -> {
            bool.filter(f -> f.term(t -> t.field("status").value(JobStatus.APPROVED.getValue())));

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = normalizeKeyword(keyword);
                String originalKeyword = keyword.trim();

                bool.must(m -> m.bool(primary -> {
                    primary.should(sq -> sq.bool(jobTitle -> {
                        jobTitle.should(exact -> exact.term(t -> t
                                .field("jobTitle.keyword")
                                .value(originalKeyword)
                                .boost(12.0f)));

                        jobTitle.should(phrase -> phrase.matchPhrase(mp -> mp
                                .field("jobTitle")
                                .query(normalizedKeyword)
                                .slop(1)
                                .boost(9.0f)));

                        jobTitle.should(mm -> mm.match(mt -> mt
                                .field("jobTitle")
                                .query(normalizedKeyword)
                                .operator(Operator.And)
                                .fuzziness("1")
                                .boost(8.0f)));

                        jobTitle.minimumShouldMatch("1");
                        return jobTitle;
                    }));

                    primary.should(sq -> sq.bool(company -> {
                        company.should(exact -> exact.term(t -> t
                                .field("companyName.keyword")
                                .value(originalKeyword)
                                .boost(6.0f)));

                        company.should(phrase -> phrase.matchPhrase(mp -> mp
                                .field("companyName")
                                .query(normalizedKeyword)
                                .slop(1)
                                .boost(5.0f)));

                        company.should(mm -> mm.match(mt -> mt
                                .field("companyName")
                                .query(normalizedKeyword)
                                .operator(Operator.And)
                                .fuzziness("1")
                                .boost(4.5f)));

                        company.minimumShouldMatch("1");
                        return company;
                    }));

                    primary.minimumShouldMatch("1");
                    return primary;
                }));

                bool.should(s -> s.match(mq -> mq
                        .field("jobDescription")
                        .query(normalizedKeyword)
                        .operator(Operator.And)
                        .boost(1.0f)));
                bool.should(s -> s.match(mq -> mq
                        .field("requirement")
                        .query(normalizedKeyword)
                        .operator(Operator.And)
                        .boost(1.0f)));

                bool.should(s -> s.matchPhrasePrefix(mpp -> mpp
                        .field("jobDescription")
                        .query(normalizedKeyword)
                        .boost(0.5f)));
                bool.should(s -> s.matchPhrasePrefix(mpp -> mpp
                        .field("requirement")
                        .query(normalizedKeyword)
                        .boost(0.5f)));
            }

            if (postedWithinDays != null && postedWithinDays > 0) {
                OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
                OffsetDateTime from = nowUtc.minusDays(postedWithinDays);
                String fromStr = from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                bool.filter(f -> f.range(r -> r
                        .field("createdAt")
                        .gte(JsonData.of(fromStr))));
            }

            if (!industryIdList.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("industries")
                        .terms(tv -> tv.value(industryIdList.stream()
                                .map(id -> FieldValue.of(String.valueOf(id)))
                                .toList()))));
            }

            if (!provinceIdList.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("provinces")
                        .terms(tv -> tv.value(provinceIdList.stream().map(FieldValue::of).toList()))));
            }

            if (jobLevels != null && !jobLevels.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("jobLevel")
                        .terms(tv -> tv.value(jobLevels.stream().map(FieldValue::of).toList()))));
            }

            if (jobTypes != null && !jobTypes.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("jobType")
                        .terms(tv -> tv.value(jobTypes.stream().map(FieldValue::of).toList()))));
            }

            if (experienceLevels != null && !experienceLevels.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("experienceLevel")
                        .terms(tv -> tv.value(experienceLevels.stream().map(FieldValue::of).toList()))));
            }

            if (educationLevels != null && !educationLevels.isEmpty()) {
                bool.filter(f -> f.terms(t -> t.field("educationLevel")
                        .terms(tv -> tv.value(educationLevels.stream().map(FieldValue::of).toList()))));
            }

            if (salaryUnit != null && !salaryUnit.isBlank()) {
                bool.filter(f -> f.term(t -> t.field("salaryUnit").value(salaryUnit)));
                if (minSalary != null) {
                    bool.filter(f -> f.range(r -> r.field("maxSalary").gte(JsonData.of(minSalary))));
                }
                if (maxSalary != null) {
                    bool.filter(f -> f.range(r -> r.field("minSalary").lte(JsonData.of(maxSalary))));
                }
            }

            return bool;
        }));

        if (sort != null && !sort.isBlank()) {
            switch (sort) {
                case "updatedAt":
                    b.withSort(s -> s.field(f -> f.field("updatedAt").order(SortOrder.Desc)));
                    break;
                case "createdAt":
                    b.withSort(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc)));
                    break;
                case "expirationDate":
                    b.withSort(s -> s.field(f -> f.field("expirationDate").order(SortOrder.Asc)));
                    break;
                default:
                    break;
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            b.withMinScore(3.0f);
        }

        NativeQuery nq = b.withPageable(pageable).build();
        SearchHits<JobDocument> hits = operations.search(nq, JobDocument.class, IndexCoordinates.of("jobs"));

        List<Long> ids = hits.stream()
                .map(h -> Long.valueOf(h.getContent().getId()))
                .toList();

        List<Job> jobs = new ArrayList<>(ids.isEmpty() ? List.of() : jobRepository.findAllById(ids));

        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            order.put(ids.get(i), i);
        }

        jobs.sort(Comparator.comparingInt(j -> order.getOrDefault(j.getId(), Integer.MAX_VALUE)));

        Page<Job> page = new PageImpl<>(jobs, pageable, hits.getTotalHits());

        return PageResponse.<List<JobResponse>>builder()
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(page.getContent().stream().map(jobMapper::toDTO).toList())
                .build();
    }

}
