package antigravity.policy.discount;

import antigravity.domain.type.DiscountType;

public interface DiscountPolicy {
    Integer getDiscountValue(DiscountType discountType,
                             Integer price, Integer discountValue);
}
