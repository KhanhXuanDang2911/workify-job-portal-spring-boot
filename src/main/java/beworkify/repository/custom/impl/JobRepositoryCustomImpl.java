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
        "select p, count(j.id) from Province p "
            + "left join p.locations jl "
            + "left join jl.job j on j.status = :status "
            + "group by p "
            + "order by count(distinct j.id) desc";
    return entityManager
        .createQuery(jpql, Object[].class)
        .setMaxResults(limit)
        .setParameter("status", JobStatus.APPROVED)
        .getResultList();
  }

  @Override
  public List<Object[]> getPopularIndustries(int limit) {
    String jpql =
        "select i, count(j.id) from Industry i "
            + "left join i.jobIndustries ji "
            + "left join ji.job j on j.status = :status "
            + "group by i "
            + "order by count(distinct j.id) desc";
    return entityManager
        .createQuery(jpql, Object[].class)
        .setMaxResults(limit)
        .setParameter("status", JobStatus.APPROVED)
        .getResultList();
  }
}
