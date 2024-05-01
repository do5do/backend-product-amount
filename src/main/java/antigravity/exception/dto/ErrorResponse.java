package antigravity.exception.dto;

import antigravity.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final ErrorCode code;
    private final String message;

    public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode,
                                                   String message) {
        return ResponseEntity.status(errorCode.getStatus().value())
                .body(new ErrorResponse(errorCode, message));
    }
}
