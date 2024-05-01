package antigravity.policy.discount;

import antigravity.domain.type.DiscountType;
import antigravity.exception.ProductException;

import static antigravity.exception.ErrorCode.INTERNAL_ERROR;

public class CodeDiscountPolicy implements DiscountPolicy {
    @Override
    public Integer getDiscountValue(DiscountType discountType,
                                    Integer price, Integer discountValue) {
        if (discountType != DiscountType.PERCENT || discountValue > 100) {
            throw new ProductException(INTERNAL_ERROR);
        }
        return (int) (price * (discountValue / 100.0));
    }
}
