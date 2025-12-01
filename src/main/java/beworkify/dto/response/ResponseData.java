package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Generic DTO for API response data. Wraps the actual data with status and message.
 *
 * @param <T> Type of the data.
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
  private int status;
  private String message;
  private T data;
}
