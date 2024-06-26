package antigravity.domain.entity;

import antigravity.domain.common.BaseTimeEntity;
import antigravity.domain.type.DiscountType;
import antigravity.domain.type.PromotionType;
import antigravity.policy.discount.DiscountPolicy;
import antigravity.policy.discount.DiscountPolicyFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Promotion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", nullable = false)
    private PromotionType promotionType; // 쿠폰 타입 (쿠폰, 코드)

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // WON : 금액 할인, PERCENT : %할인

    @Column(nullable = false)
    private Integer discountValue; // 할인 금액 or 할인 %

    @Column(nullable = false)
    private LocalDate useStartedAt; // 쿠폰 사용 가능 시작 기간

    @Column(nullable = false)
    private LocalDate useEndedAt; // 쿠폰 사용 가능 종료 기간

    @Builder
    public Promotion(PromotionType promotionType, String name,
                     DiscountType discountType, Integer discountValue,
                     LocalDate useStartedAt, LocalDate useEndedAt) {
        this.promotionType = promotionType;
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.useStartedAt = useStartedAt;
        this.useEndedAt = useEndedAt;
    }

    public Integer getDiscountValue(Integer price) {
        DiscountPolicy discountPolicy = DiscountPolicyFactory.of(promotionType);
        return discountPolicy.getDiscountValue(discountType, price, discountValue);
    }
}
