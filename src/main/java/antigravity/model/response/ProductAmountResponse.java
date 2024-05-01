package antigravity.model.response;

import antigravity.domain.entity.Product;
import antigravity.exception.ProductException;
import lombok.Builder;
import lombok.Getter;

import static antigravity.exception.ErrorCode.INVALID_DISCOUNT_PRICE;

@Getter
@Builder
public class ProductAmountResponse {
    private String name; // 상품명
    private Integer originPrice; // 상품 기존 가격
    private Integer discountPrice; // 총 할인 금액
    private Integer finalPrice; // 확정 상품 가격

    public static ProductAmountResponse from(Product product, Integer totalDiscount) {
        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(totalDiscount)
                .finalPrice(getFinalPrice(product.getPrice(), totalDiscount))
                .build();
    }

    private static int getFinalPrice(Integer originPrice, Integer totalDiscount) {
        if (originPrice < totalDiscount) {
            throw new ProductException(INVALID_DISCOUNT_PRICE);
        }

        // 1000 단위 이하 절사
        int finalPrice = originPrice - totalDiscount;
        return finalPrice > 1000 ? finalPrice / 1000 * 1000 : finalPrice;
    }
}
