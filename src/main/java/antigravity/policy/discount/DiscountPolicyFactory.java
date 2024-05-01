package antigravity.policy.discount;

import antigravity.domain.type.PromotionType;
import antigravity.exception.ProductException;

import static antigravity.exception.ErrorCode.INVALID_PROMOTION_TYPE;

public class DiscountPolicyFactory {
    private static final CouponDiscountPolicy couponDiscountPolicy = new CouponDiscountPolicy();
    private static final CodeDiscountPolicy codeDiscountPolicy = new CodeDiscountPolicy();

    public static DiscountPolicy of(PromotionType promotionType) {
        switch (promotionType) {
            case COUPON -> {
                return couponDiscountPolicy;
            }
            case CODE -> {
                return codeDiscountPolicy;
            }
            default -> throw new ProductException(INVALID_PROMOTION_TYPE);
        }
    }
}
