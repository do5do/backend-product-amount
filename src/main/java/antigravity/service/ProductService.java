package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.dto.PriceDto;
import antigravity.exception.ProductException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static antigravity.constants.MsgFormat.ADD_NAME;
import static antigravity.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionProductsRepository promotionProductsRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
        product.validatePrice();

        List<Promotion> promotions =
                promotionRepository.findByIdIn(request.getCouponIds());
        validatePromotion(product, promotions);

        return applyPromotion(product, promotions);
    }

    private ProductAmountResponse applyPromotion(
            Product product, List<Promotion> promotions) {
        ProductAmountResponse response = ProductAmountResponse.fromEntity(product);

        promotions.forEach(promotion -> {
            PriceDto priceDto = promotion.calculatePrice(response.getFinalPrice());
            response.addPriceAndDiscount(priceDto.price(),
                    priceDto.discountValue());
        });

        response.truncateFinalPrice();
        return response;
    }

    private void validatePromotion(Product product, List<Promotion> promotions) {
        // 유효 시간 확인
        LocalDate now = LocalDate.now();
        promotions.forEach(promotion -> {
            if (promotion.getUseStartedAt().isAfter(now)
                    || promotion.getUseEndedAt().isBefore(now)) {
                throw new ProductException(NOT_PERIOD_PROMOTION, String.format(ADD_NAME,
                        promotion.getName(), NOT_PERIOD_PROMOTION.getMessage()));
            }
        });

        // 매핑 확인
        if (promotions.isEmpty() || !promotionProductsRepository
                .existsByProductAndPromotionIn(product, promotions)) {
            throw new ProductException(UNMATCHED_PROMOTION, String.format(ADD_NAME,
                    product.getName(), UNMATCHED_PROMOTION.getMessage()));
        }
    }
}
