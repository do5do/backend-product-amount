package antigravity.model.response;

import antigravity.domain.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductAmountResponse {
    private String name; // 상품명
    private Integer originPrice; // 상품 기존 가격
    private Integer discountPrice; // 총 할인 금액
    private Integer finalPrice; // 확정 상품 가격

    public static ProductAmountResponse fromEntity(Product product) {
        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(0)
                .finalPrice(product.getPrice())
                .build();
    }

    public void addPriceAndDiscount(Integer calcPrice, Integer discountValue) {
        finalPrice = calcPrice;
        discountPrice += discountValue;
    }

    public void truncateFinalPrice() { // 1000 단위 이하 절사
        finalPrice = finalPrice / 1000 * 1000;
    }
}
