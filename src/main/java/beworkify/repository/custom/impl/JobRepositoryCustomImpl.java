package beworkify.repository.custom.impl;

import beworkify.enumeration.JobStatus;
import beworkify.repository.custom.JobRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepositoryCustomImpl implements JobRepositoryCustom {
  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Object[]> getPopularProvinces(int limit) {
    String jpql =
        "SELECT p, COUNT(j.id) "
            + "FROM Province p "
            + "LEFT JOIN p.locations jl "
            + "LEFT JOIN jl.job j ON j.status = :status "
            + "GROUP BY p "
            + "ORDER BY COUNT(DISTINCT j.id) DESC";
    return entityManager
        .createQuery(jpql, Object[].class)
        .setMaxResults(limit)
        .setParameter("status", JobStatus.APPROVED)
        .getResultList();
  }

  @Override
  public List<Object[]> getPopularIndustries(int limit) {
    String jpql =
        "SELECT i, COUNT(j.id) "
            + "FROM Industry i "
            + "LEFT JOIN i.jobIndustries ji "
            + "LEFT JOIN ji.job j ON j.status = :status "
            + "GROUP BY i "
            + "ORDER BY COUNT(DISTINCT j.id) DESC";
    return entityManager
        .createQuery(jpql, Object[].class)
        .setMaxResults(limit)
        .setParameter("status", JobStatus.APPROVED)
        .getResultList();
  }
}
