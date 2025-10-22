package beworkify.configuration.initializer;

import beworkify.entity.CategoryJob;
import beworkify.entity.Industry;
import beworkify.repository.CategoryJobRepository;
import beworkify.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(300)
public class IndustryInitializer implements CommandLineRunner {

    private final IndustryRepository industryRepository;
    private final CategoryJobRepository categoryJobRepository;

    @Override
    public void run(String... args) {
        try {
            if (industryRepository.count() > 0) {
                log.info("Industries already initialized, skipping seeding.");
                return;
            }

            Map<String, List<Entry>> data = industryEntries();

            int inserted = 0;
            for (Map.Entry<String, List<Entry>> categoryEntry : data.entrySet()) {
                String categoryName = categoryEntry.getKey();
                CategoryJob categoryJob = categoryJobRepository.findByName(categoryName)
                        .orElseThrow(() -> new RuntimeException("CategoryJob not found: " + categoryName));

                for (Entry e : categoryEntry.getValue()) {
                    boolean existsByVi = industryRepository.existsByName(e.vi);
                    boolean existsByEn = industryRepository.existsByEngName(e.en);

                    if (!existsByVi && !existsByEn) {
                        Industry ind = Industry.builder()
                                .name(e.vi)
                                .engName(e.en)
                                .description(null)
                                .categoryJob(categoryJob)
                                .build();

                        industryRepository.save(ind);
                        inserted++;
                    }
                }
            }

            log.info("Industry initialization completed: {} records inserted", inserted);
        } catch (Exception ex) {
            log.error("Failed to initialize industries: {}", ex.getMessage(), ex);
        }
    }

    private Map<String, List<Entry>> industryEntries() {
        Map<String, List<Entry>> map = new LinkedHashMap<>();

        map.put("Bộ phận hỗ trợ", List.of(
                new Entry("Biên phiên dịch / Thông dịch viên", "Translation / Interpretation"),
                new Entry("Tiếng Nhật", "Japanese Language"),
                new Entry("Pháp lý / Luật", "Legal / Law"),
                new Entry("Thư ký / Hành chính", "Secretary / Administration")
        ));

        map.put("Hỗ trợ sản xuất", List.of(
                new Entry("Quản lý chất lượng (QA / QC)", "Quality Assurance / Quality Control"),
                new Entry("Vận chuyển / Giao thông / Kho bãi", "Logistics / Transportation / Warehouse"),
                new Entry("Vật tư / Thu mua", "Procurement / Purchasing"),
                new Entry("Xuất nhập khẩu / Ngoại thương", "Import / Export / International Trade")
        ));

        map.put("Xây dựng / Bất động sản", List.of(
                new Entry("Bất động sản", "Real Estate"),
                new Entry("Kiến trúc", "Architecture"),
                new Entry("Nội thất / Ngoại thất", "Interior / Exterior Design"),
                new Entry("Xây dựng", "Construction")
        ));

        map.put("Truyền thông", List.of(
                new Entry("Báo chí / Biên tập viên / Xuất bản", "Journalism / Editing / Publishing"),
                new Entry("Nghệ thuật / Thiết kế / Giải trí", "Art / Design / Entertainment"),
                new Entry("Viễn thông", "Telecommunications")
        ));

        map.put("Dịch vụ", List.of(
                new Entry("An Ninh / Bảo Vệ", "Security / Guard"),
                new Entry("Bán lẻ / Bán sỉ", "Retail / Wholesale"),
                new Entry("Chăm sóc sức khỏe / Y tế", "Healthcare / Medical"),
                new Entry("Dịch vụ khách hàng", "Customer Service"),
                new Entry("Giáo dục / Đào tạo / Thư viện", "Education / Training / Library"),
                new Entry("Phi chính phủ / Phi lợi nhuận", "Non-Governmental / Non-Profit")
        ));

        map.put("Dịch vụ tài chính", List.of(
                new Entry("Bảo hiểm", "Insurance"),
                new Entry("Kế toán / Kiểm toán", "Accounting / Auditing"),
                new Entry("Ngân hàng / Chứng khoán", "Banking / Securities"),
                new Entry("Tài chính / Đầu tư", "Finance / Investment")
        ));

        map.put("Giao dịch khách hàng", List.of(
                new Entry("Bán hàng / Kinh doanh", "Sales / Business Development"),
                new Entry("Hàng gia dụng", "Household Goods"),
                new Entry("Quảng cáo / Khuyến mãi / Đối ngoại", "Advertising / Promotion / Public Relations"),
                new Entry("Thời trang", "Fashion"),
                new Entry("Tiếp thị", "Marketing"),
                new Entry("Tư vấn dịch vụ khách hàng", "Customer Service Consulting")
        ));

        map.put("Khách sạn / Du lịch", List.of(
                new Entry("Du lịch", "Tourism"),
                new Entry("Khách sạn", "Hotel"),
                new Entry("Nhà hàng / Dịch vụ ăn uống", "Restaurant / Food Service")
        ));

        map.put("Tư vấn chuyên môn", List.of(
                new Entry("Tư vấn kỹ thuật", "Technical Consulting"),
                new Entry("Tư vấn logistics / marketing / thương mại", "Logistics / Marketing / Trade Consulting"),
                new Entry("Tư vấn quản trị / pháp lý / nhân sự", "Management / Legal / HR Consulting")
        ));

        map.put("IT - Công nghệ thông tin", List.of(
                new Entry("CNTT - Phần cứng / Mạng", "IT - Hardware / Networking"),
                new Entry("CNTT - Phần mềm", "IT - Software")
        ));

        map.put("Kỹ thuật", List.of(
                new Entry("Bảo trì / Sửa chữa", "Maintenance / Repair"),
                new Entry("Điện lạnh / Nhiệt lạnh", "Refrigeration / HVAC"),
                new Entry("Dược / Sinh học", "Pharmaceutical / Biology"),
                new Entry("Điện / Điện tử", "Electrical / Electronics"),
                new Entry("Kỹ thuật ứng dụng / Cơ khí", "Applied Engineering / Mechanical"),
                new Entry("Môi trường / Xử lý chất thải", "Environment / Waste Treatment")
        ));

        map.put("Sản xuất", List.of(
                new Entry("An toàn lao động", "Occupational Safety"),
                new Entry("Dầu khí / Khoáng sản", "Oil & Gas / Mining"),
                new Entry("Dệt may / Da giày", "Textile / Footwear"),
                new Entry("Đồ gỗ", "Woodwork / Furniture"),
                new Entry("Hóa chất / Sinh học / Thực phẩm", "Chemical / Biological / Food"),
                new Entry("Nông nghiệp / Lâm nghiệp", "Agriculture / Forestry"),
                new Entry("Ô tô", "Automotive"),
                new Entry("Sản xuất / Vận hành sản xuất", "Manufacturing / Production Operation"),
                new Entry("Thủy hải sản", "Fisheries / Aquaculture")
        ));

        map.put("Theo đối tượng", List.of(
                new Entry("Lao động phổ thông", "Manual Labor"),
                new Entry("Mới tốt nghiệp / Thực tập", "Fresh Graduate / Internship"),
                new Entry("Người nước ngoài", "Foreigners"),
                new Entry("Quản lý điều hành", "Management / Executive")
        ));

        map.put("Khác", List.of(
                new Entry("Khác", "Others")
        ));

        return map;
    }

    private record Entry(String vi, String en) {}
}
