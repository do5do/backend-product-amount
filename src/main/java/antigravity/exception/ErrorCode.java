package antigravity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_PRODUCT(NOT_FOUND, "상품을 찾을 수 없습니다."),
    NOT_FOUND_PROMOTION(NOT_FOUND, "프로모션을 찾을 수 없습니다."),
    UNMATCHED_PROMOTION(BAD_REQUEST, "상품에 적용할 수 없는 프로모선입니다."),
    NOT_PERIOD_PROMOTION(BAD_REQUEST, "프로모션 사용 기간이 아닙니다."),
    INVALID_PROMOTION_TYPE(BAD_REQUEST, "지원하지 않는 할인 정책입니다."),
    INVALID_PRODUCT_PRICE(BAD_REQUEST, "허용되지 않는 상품 가격입니다."),
    INVALID_DISCOUNT_PRICE(BAD_REQUEST, "잘못된 할인 가격입니다."),

    // global
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    KEY_CONFLICT(CONFLICT, "현재 리소스 상태와의 충돌로 인해 요청을 완료할 수 없습니다."),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
