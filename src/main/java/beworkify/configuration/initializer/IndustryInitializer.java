package beworkify.configuration.initializer;

import beworkify.entity.Industry;
import beworkify.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(300)
public class IndustryInitializer implements CommandLineRunner {

    private final IndustryRepository industryRepository;

    @Override
    public void run(String... args) {
        try {
            if (industryRepository.count() > 0) {
                log.info("Industries already initialized, skipping seeding.");
                return;
            }

            List<Entry> entries = industryEntries();

            int inserted = 0;
            for (Entry e : entries) {
                boolean existsByVi = industryRepository.existsByName(e.vi);
                boolean existsByEn = industryRepository.existsByEngName(e.en);
                if (!existsByVi && !existsByEn) {
                    Industry ind = Industry.builder()
                            .name(e.vi)
                            .engName(e.en)
                            .description(null)
                            .build();
                    industryRepository.save(ind);
                    inserted++;
                }
            }

            if (inserted > 0) {
                log.info("Industry initialization completed: {} new records inserted", inserted);
            } else {
                log.info("Industry initialization skipped: no new records to insert");
            }
        } catch (Exception ex) {
            log.error("Failed to initialize industries: {}", ex.getMessage(), ex);
        }
    }

    private List<Entry> industryEntries() {
        List<Entry> list = new ArrayList<>();
        list.add(new Entry("An Ninh / Bảo Vệ", "Security / Protection"));
        list.add(new Entry("An Toàn Lao Động", "Occupational Safety"));
        list.add(new Entry("Bán hàng / Kinh doanh", "Sales / Business"));
        list.add(new Entry("Bán lẻ / Bán sỉ", "Retail / Wholesale"));
        list.add(new Entry("Báo chí / Biên tập viên / Xuất bản", "Journalism / Editing / Publishing"));
        list.add(new Entry("Bảo hiểm", "Insurance"));
        list.add(new Entry("Bảo trì / Sửa chữa", "Maintenance / Repair"));
        list.add(new Entry("Bất động sản", "Real Estate"));
        list.add(new Entry("Biên phiên dịch / Thông dịch viên", "Translation / Interpretation"));
        list.add(new Entry("Tiếng Nhật", "Japanese Language"));
        list.add(new Entry("Chăm sóc sức khỏe / Y tế", "Healthcare / Medical"));
        list.add(new Entry("CNTT - Phần cứng / Mạng", "IT - Hardware / Networking"));
        list.add(new Entry("CNTT - Phần mềm", "IT - Software"));
        list.add(new Entry("Dầu khí / Khoáng sản", "Oil & Gas / Mining"));
        list.add(new Entry("Dệt may / Da giày", "Textile / Footwear"));
        list.add(new Entry("Dịch vụ khách hàng", "Customer Service"));
        list.add(new Entry("Điện lạnh / Nhiệt lạnh", "Refrigeration / Air Conditioning"));
        list.add(new Entry("Du lịch", "Tourism"));
        list.add(new Entry("Dược / Sinh học", "Pharmacy / Biotechnology"));
        list.add(new Entry("Điện / Điện tử", "Electrical / Electronics"));
        list.add(new Entry("Đồ Gỗ", "Woodworking / Furniture"));
        list.add(new Entry("Giáo dục / Đào tạo / Thư viện", "Education / Training / Library"));
        list.add(new Entry("Hàng gia dụng", "Household Goods"));
        list.add(new Entry("Hóa chất / Sinh hóa / Thực phẩm", "Chemicals / Biochemistry / Food"));
        list.add(new Entry("Kế toán / Kiểm toán", "Accounting / Auditing"));
        list.add(new Entry("Khách sạn", "Hotel / Hospitality"));
        list.add(new Entry("Kiến trúc", "Architecture"));
        list.add(new Entry("Kỹ thuật ứng dụng / Cơ khí", "Applied Engineering / Mechanical Engineering"));
        list.add(new Entry("Lao động phổ thông", "General Labor"));
        list.add(new Entry("Môi trường / Xử lý chất thải", "Environment / Waste Treatment"));
        list.add(new Entry("Mới tốt nghiệp / Thực tập", "Fresh Graduate / Internship"));
        list.add(new Entry("Ngân hàng / Chứng khoán", "Banking / Securities"));
        list.add(new Entry("Nghệ thuật / Thiết kế / Giải trí", "Art / Design / Entertainment"));
        list.add(new Entry("Người nước ngoài", "Expatriate / Foreign Worker"));
        list.add(new Entry("Nhà hàng / Dịch vụ ăn uống", "Restaurant / Food Service"));
        list.add(new Entry("Nhân sự", "Human Resources"));
        list.add(new Entry("Nội thất / Ngoại thất", "Interior / Exterior Design"));
        list.add(new Entry("Nông nghiệp / Lâm nghiệp", "Agriculture / Forestry"));
        list.add(new Entry("Ô tô", "Automotive"));
        list.add(new Entry("Pháp lý / Luật", "Legal / Law"));
        list.add(new Entry("Phi chính phủ / Phi lợi nhuận", "Non-Governmental / Non-Profit"));
        list.add(new Entry("Quản lý chất lượng (QA / QC)", "Quality Assurance / Quality Control"));
        list.add(new Entry("Quản lý điều hành", "Management / Administration"));
        list.add(new Entry("Quảng cáo / Khuyến mãi / Đối ngoại", "Advertising / Promotion / Public Relations"));
        list.add(new Entry("Sản xuất / Vận hành sản xuất", "Manufacturing / Production Operations"));
        list.add(new Entry("Tài chính / Đầu tư", "Finance / Investment"));
        list.add(new Entry("Thời trang", "Fashion"));
        list.add(new Entry("Thuỷ Hải Sản", "Seafood / Aquaculture"));
        list.add(new Entry("Thư ký / Hành chánh", "Secretary / Administration"));
        list.add(new Entry("Tiếp thị", "Marketing"));
        list.add(new Entry("Tư vấn dịch vụ khách hàng", "Customer Service Consulting"));
        list.add(new Entry("Vận chuyển / Giao thông / Kho bãi", "Transportation / Logistics / Warehousing"));
        list.add(new Entry("Vật tư / Thu mua", "Procurement / Purchasing"));
        list.add(new Entry("Viễn Thông", "Telecommunications"));
        list.add(new Entry("Xây dựng", "Construction"));
        list.add(new Entry("Xuất nhập khẩu / Ngoại thương", "Import / Export / Foreign Trade"));
        list.add(new Entry("Khác", "Others / Miscellaneous"));
        list.add(new Entry("Tư vấn kỹ thuật", "Technical Consulting"));
        list.add(new Entry("Tư vấn quản trị / pháp lý / nhân sự", "Management / Legal / HR Consulting"));
        list.add(new Entry("Tư vấn logistics / marketing / thương mại", "Logistics / Marketing / Business Consulting"));
        return list;
    }

    private record Entry(String vi, String en) {
    }
}
