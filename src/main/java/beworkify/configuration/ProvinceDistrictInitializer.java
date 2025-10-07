package beworkify.configuration;

import beworkify.entity.District;
import beworkify.entity.Province;
import beworkify.repository.DistrictRepository;
import beworkify.repository.ProvinceRepository;
import beworkify.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(200)
public class ProvinceDistrictInitializer implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @Override
    public void run(String... args) {
        try {
            if (provinceRepository.count() > 0) {
                log.info("Provinces already initialized, skipping API call.");
                return;
            }

            log.info("=== Initializing provinces and districts from open-api.vn ===");

            String url = "https://provinces.open-api.vn/api/v1/?depth=2";
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to fetch provinces: {}", response.getStatusCode());
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<ProvinceInput> provinces = mapper.readValue(response.getBody(), new TypeReference<>() {});

            for (ProvinceInput p : provinces) {
                if (p.code == null)
                    continue;

                String cleanName = p.name.replaceAll("(?i)^(Tỉnh|Thành phố)\\s+", "").trim();

                Province province = new Province();
                province.setCode(String.valueOf(p.code));
                province.setName(cleanName);
                province.setEngName(toEnglishName(cleanName));
                province.setProvinceSlug(AppUtils.toSlug(cleanName));
                province = provinceRepository.save(province);

                if (p.districts != null) {
                    for (DistrictInput d : p.districts) {
                        if (d.code == null)
                            continue;

                        District district = new District();
                        district.setCode(String.valueOf(d.code));
                        district.setName(d.name);
                        district.setDistrictSlug(AppUtils.toSlug(d.name));
                        district.setProvince(province);
                        districtRepository.save(district);
                    }
                }

                log.info("Inserted province {} (engName={}) with {} districts",
                        province.getName(), province.getEngName(),
                        (p.districts != null ? p.districts.size() : 0));
            }

            log.info("=== Province and District initialization completed ===");

        } catch (Exception ex) {
            log.error("Failed to initialize provinces/districts: {}", ex.getMessage(), ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ProvinceInput {
        public String name;
        public Integer code;
        @JsonProperty("districts")
        public List<DistrictInput> districts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DistrictInput {
        public String name;
        public Integer code;
    }

    private static String toEnglishName(String vietnamese) {
        if (vietnamese == null) return null;
        String normalized = Normalizer.normalize(vietnamese, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccent = pattern.matcher(normalized).replaceAll("");
        withoutAccent = withoutAccent.replace('đ', 'd').replace('Đ', 'D');
        return withoutAccent.trim();
    }
}
