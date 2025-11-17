
package beworkify.configuration;

import beworkify.search.document.JobDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class ElasticsearchConfig {

	private final ElasticsearchOperations operations;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void initIndex() throws IOException {
		ClassPathResource settingsRes = new ClassPathResource("elasticsearch/jobs-settings.json");
		Map<String, Object> settings;
		try (InputStream is = settingsRes.getInputStream()) {
			settings = objectMapper.readValue(is, new TypeReference<>() {
			});
		}

		IndexOperations indexOps = operations.indexOps(JobDocument.class);
		if (!indexOps.exists()) {
			indexOps.create(settings);
			indexOps.putMapping(indexOps.createMapping());
		}
	}
}
