package beworkify.repository.custom.impl;

import beworkify.enumeration.JobStatus;
import beworkify.repository.custom.CategoryJobRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryJobRepositoryCustomImpl implements CategoryJobRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findCategoryJobWithIndustryCount() {
        String query = "select c, i, count(distinct j.id), 0 " +
                "from CategoryJob c " +
                "left join c.industries i " +
                "left join i.jobIndustries ji " +
                "left join ji.job j on j.status = :status " +
                "group by i, c " +
                "order by count(distinct j.id) desc";
        return entityManager.createQuery(query, Object[].class)
                        .setParameter("status", JobStatus.APPROVED)
                        .getResultList();
    }

}
