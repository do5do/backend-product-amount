package antigravity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // global
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    KEY_CONFLICT(CONFLICT, "현재 리소스 상태와의 충돌로 인해 요청을 완료할 수 없습니다."),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
