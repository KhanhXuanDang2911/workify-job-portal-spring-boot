package beworkify.configuration.initializer;

import beworkify.entity.CategoryJob;
import beworkify.repository.CategoryJobRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(200)
public class CategoryJobInitializer implements CommandLineRunner {

  private final CategoryJobRepository categoryJobRepository;

  @Override
  public void run(String... args) {
    try {
      if (categoryJobRepository.count() > 0) {
        log.info("CategoryJob already initialized, skipping seeding.");
        return;
      }

      List<Entry> entries =
          Arrays.asList(
              new Entry("Bộ phận hỗ trợ", "Administrative / Support Department"),
              new Entry("Hỗ trợ sản xuất", "Production Support"),
              new Entry("Xây dựng / Bất động sản", "Construction / Real Estate"),
              new Entry("Truyền thông", "Media / Communication"),
              new Entry("Dịch vụ", "Services"),
              new Entry("Dịch vụ tài chính", "Financial Services"),
              new Entry("Giao dịch khách hàng", "Customer Relations"),
              new Entry("Khách sạn / Du lịch", "Hospitality / Tourism"),
              new Entry("Tư vấn chuyên môn", "Professional Consulting"),
              new Entry("IT - Công nghệ thông tin", "Information Technology"),
              new Entry("Kỹ thuật", "Engineering"),
              new Entry("Sản xuất", "Manufacturing"),
              new Entry("Theo đối tượng", "By Target Group"),
              new Entry("Khác", "Others"));

      for (Entry e : entries) {
        if (!categoryJobRepository.existsByName(e.vi)) {
          categoryJobRepository.save(
              CategoryJob.builder().name(e.vi).engName(e.en).description(null).build());
        }
      }

      log.info("CategoryJob initialization completed.");
    } catch (Exception ex) {
      log.error("Failed to initialize CategoryJob: {}", ex.getMessage(), ex);
    }
  }

  private record Entry(String vi, String en) {}
}
