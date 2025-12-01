package beworkify.repository.custom.impl;

import beworkify.enumeration.JobStatus;
import beworkify.repository.custom.CategoryJobRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryJobRepositoryCustomImpl implements CategoryJobRepositoryCustom {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Object[]> findCategoryJobWithIndustryCount() {
    String query =
        "SELECT c, i, COUNT(DISTINCT j.id), 0 "
            + "FROM CategoryJob c "
            + "LEFT JOIN c.industries i "
            + "LEFT JOIN i.jobIndustries ji "
            + "LEFT JOIN ji.job j ON j.status = :status "
            + "GROUP BY i, c "
            + "ORDER BY COUNT(DISTINCT j.id) DESC";
    return entityManager
        .createQuery(query, Object[].class)
        .setParameter("status", JobStatus.APPROVED)
        .getResultList();
  }
}
