
package beworkify.dto.response;

import beworkify.enumeration.StatusPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PostResponse extends BaseResponse {
	private String title;
	private String excerpt;
	private String content;
	private String contentText;
	private String thumbnailUrl;
	private String tags;
	private String slug;
	private Integer readingTimeMinutes;
	private CategoryPostResponse category;
	private UserSummaryResponse author;
	private StatusPost status;
}
