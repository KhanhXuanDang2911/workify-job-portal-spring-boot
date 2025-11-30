package beworkify.repository.custom;

import java.util.List;

public interface JobRepositoryCustom {
  List<Object[]> getPopularProvinces(int limit);

  List<Object[]> getPopularIndustries(int limit);
}
