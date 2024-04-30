package antigravity.exception;

import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public ProductException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

    public ProductException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
