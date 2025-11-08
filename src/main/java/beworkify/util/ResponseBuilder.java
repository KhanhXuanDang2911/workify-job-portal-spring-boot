
package beworkify.util;

import beworkify.dto.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
	public static ResponseEntity<ResponseData<Void>> noData(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				.body(ResponseData.<Void>builder().status(status.value()).message(message).build());
	}

	public static ResponseEntity<Void> noContent() {
		return ResponseEntity.noContent().build();
	}

	public static <T> ResponseEntity<ResponseData<T>> withData(HttpStatus status, String message, T data) {
		return ResponseEntity.status(status)
				.body(ResponseData.<T>builder().status(status.value()).message(message).data(data).build());
	}
}
