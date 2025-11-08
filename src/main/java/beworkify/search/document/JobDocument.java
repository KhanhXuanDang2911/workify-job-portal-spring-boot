
package beworkify.search.document;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setting(settingPath = "elasticsearch/jobs-settings.json")
@Document(indexName = "jobs")
public class JobDocument {
	@Id
	private String id;

	@MultiField(mainField = @Field(type = FieldType.Text, analyzer = "vn_folded", searchAnalyzer = "vn_search"), otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword)})
	private String jobTitle;

	@MultiField(mainField = @Field(type = FieldType.Text, analyzer = "vn_folded", searchAnalyzer = "vn_search"), otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword)})
	private String companyName;

	@Field(type = FieldType.Text, analyzer = "vn_folded", searchAnalyzer = "vn_search")
	private String jobDescription;

	@Field(type = FieldType.Text, analyzer = "vn_folded", searchAnalyzer = "vn_search")
	private String requirement;

	@Field(type = FieldType.Keyword)
	private List<Long> industries;

	@Field(type = FieldType.Long)
	private List<Long> provinces;

	@Field(type = FieldType.Date)
	private LocalDate expirationDate;

	@Field(type = FieldType.Keyword)
	private String status;

	@Field(type = FieldType.Date, format = {DateFormat.date_time})
	private OffsetDateTime createdAt;

	@Field(type = FieldType.Date, format = {DateFormat.date_time})
	private OffsetDateTime updatedAt;

	@Field(type = FieldType.Double)
	private Double minSalary;

	@Field(type = FieldType.Double)
	private Double maxSalary;

	@Field(type = FieldType.Keyword)
	private String salaryType;

	@Field(type = FieldType.Keyword)
	private String salaryUnit;

	@Field(type = FieldType.Keyword)
	private String jobLevel;

	@Field(type = FieldType.Keyword)
	private String jobType;

	@Field(type = FieldType.Keyword)
	private String experienceLevel;

	@Field(type = FieldType.Keyword)
	private String educationLevel;

	@CompletionField
	private Completion suggest;
}
