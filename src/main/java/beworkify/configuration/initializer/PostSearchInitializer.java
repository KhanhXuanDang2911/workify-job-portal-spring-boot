
package beworkify.configuration.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(100)
public class PostSearchInitializer implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) {
		try {
			setupAdvancedIndexes();
			log.info("Advanced post search indexes setup completed");
		} catch (Exception e) {
			log.error("Failed to setup advanced indexes: {}", e.getMessage());
		}
	}

	private void setupAdvancedIndexes() {
		executeSQL("CREATE EXTENSION IF NOT EXISTS pg_trgm", "pg_trgm extension enabled");

		executeSQL("""
				CREATE INDEX IF NOT EXISTS idx_posts_fulltext_search
				ON posts USING GIN(to_tsvector('english', title || ' ' || COALESCE(content_text, '')))
				""", "Full-text search index created");

		executeSQL("""
				CREATE INDEX IF NOT EXISTS idx_posts_title_trgm
				ON posts USING GIN(title gin_trgm_ops)
				""", "Title trigram index created");

		executeSQL("""
				CREATE INDEX IF NOT EXISTS idx_posts_content_text_trgm
				ON posts USING GIN(content_text gin_trgm_ops)
				""", "Content trigram index created");

		executeSQL("""
				CREATE INDEX IF NOT EXISTS idx_posts_category_status_updated
				ON posts(category_id, status, updated_at DESC)
				WHERE status = 'PUBLIC'
				""", "Conditional composite index created");

		executeSQL("""
				CREATE INDEX IF NOT EXISTS idx_posts_status_updated
				ON posts(status, updated_at DESC)
				WHERE status = 'PUBLIC'
				""", "StatusUser updated index created");
	}

	private void executeSQL(String sql, String successMessage) {
		try {
			jdbcTemplate.execute(sql);
			log.info(successMessage);
		} catch (Exception e) {
			log.warn("Could not execute SQL [{}]: {}", sql.substring(0, Math.min(50, sql.length())), e.getMessage());
		}
	}
}
