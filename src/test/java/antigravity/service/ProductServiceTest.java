package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.type.DiscountType;
import antigravity.domain.type.PromotionType;
import antigravity.exception.ProductException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static antigravity.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    PromotionRepository promotionRepository;

    @Mock
    PromotionProductsRepository promotionProductsRepository;

    @InjectMocks
    ProductService productService;

    Long productId = 1L;
    List<Long> couponIds = List.of(1L, 2L);
    ProductInfoRequest request = ProductInfoRequest.builder()
            .productId(productId)
            .couponIds(couponIds)
            .build();
    Product product = Product.builder()
            .name("피팅노드상품")
            .price(215000)
            .build();

    @Test
    @DisplayName("성공 - 상품에 매핑된 유효한 프로모션이면 프로모션이 적용된 가격을 추출할 수 있다.")
    void getProductAmount() {
        // given
        Promotion promotion1 = Promotion.builder()
                .promotionType(PromotionType.COUPON)
                .name("30000원 할인쿠폰")
                .discountType(DiscountType.WON)
                .discountValue(30000)
                .useStartedAt(LocalDate.now().minusMonths(1))
                .useEndedAt(LocalDate.now())
                .build();

        Promotion promotion2 = Promotion.builder()
                .promotionType(PromotionType.CODE)
                .name("15% 할인코드")
                .discountType(DiscountType.PERCENT)
                .discountValue(15)
                .useStartedAt(LocalDate.now())
                .useEndedAt(LocalDate.now().plusMonths(1))
                .build();

        List<Promotion> promotions = List.of(promotion1, promotion2);

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));

        given(promotionRepository.findByIdIn(couponIds))
                .willReturn(promotions);

        given(promotionProductsRepository
                .existsByProductAndPromotionIn(product, promotions))
                .willReturn(true);

        // when
        ProductAmountResponse response = productService.getProductAmount(request);

        // then
        int totalDiscount = 30000 + (int) (215000 * 0.15);
        int finalPrice = (215000 - totalDiscount) / 1000 * 1000;

        assertEquals(totalDiscount, response.getDiscountPrice());
        assertEquals(finalPrice, response.getFinalPrice());
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 상품이면 예외가 발생한다.")
    void getProductAmount_notFound_product() {
        // given
        given(productRepository.findById(productId))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.getProductAmount(request))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(NOT_FOUND_PRODUCT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {9000, 10_000_001})
    @DisplayName("실패 - 허용되지 않은 상품 가격인 경우 예외가 발생한다.")
    void getProductAmount_invalid_price_min(Integer price) {
        // given
        Product product = Product.builder()
                .name("피팅노드상품")
                .price(price)
                .build();

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> productService.getProductAmount(request))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(INVALID_PRODUCT_PRICE.getMessage());
    }

    @Nested
    @DisplayName("프로모션 기간 검증")
    class NotPeriodPromotion {
        static Stream<Arguments> provideDates() {
            return Stream.of(
                    // 시작 전
                    Arguments.of(LocalDate.now().plusDays(1),
                            LocalDate.now().plusMonths(1)),
                    // 기간 지난
                    Arguments.of(LocalDate.now().minusMonths(1),
                            LocalDate.now().minusDays(1))
            );
        }

        @ParameterizedTest
        @MethodSource("provideDates")
        @DisplayName("실패 - 시작 전이거나 지난 프로모션이면 예외가 발생한다.")
        void getProductAmount_notPeriod_promotion(LocalDate startDate, LocalDate endDate) {
            // given
            Promotion promotion = Promotion.builder()
                    .promotionType(PromotionType.COUPON)
                    .name("30000원 할인쿠폰")
                    .discountType(DiscountType.WON)
                    .discountValue(30000)
                    .useStartedAt(startDate)
                    .useEndedAt(endDate)
                    .build();

            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));

            given(promotionRepository.findByIdIn(couponIds))
                    .willReturn(List.of(promotion));

            // when
            // then
            assertThatThrownBy(() -> productService.getProductAmount(request))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(NOT_PERIOD_PROMOTION.getMessage());
        }
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 프로모션이면 예외가 발생한다.")
    void getProductAmount_promotions_isEmpty() {
        // given
        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));

        given(promotionRepository.findByIdIn(couponIds))
                .willReturn(List.of());

        // when
        // then
        assertThatThrownBy(() -> productService.getProductAmount(request))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(NOT_FOUND_PROMOTION.getMessage());
    }

    @Test
    @DisplayName("실패 - 상품과 매핑되지 않은 프로모션이면 예외가 발생한다.")
    void getProductAmount_promotion_mapping_isNotExists() {
        // given
        Promotion promotion = Promotion.builder()
                .promotionType(PromotionType.CODE)
                .name("15% 할인코드")
                .discountType(DiscountType.PERCENT)
                .discountValue(15)
                .useStartedAt(LocalDate.now())
                .useEndedAt(LocalDate.now().plusMonths(1))
                .build();

        List<Promotion> promotions = List.of(promotion);

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));

        given(promotionRepository.findByIdIn(couponIds))
                .willReturn(promotions);

        given(promotionProductsRepository
                .existsByProductAndPromotionIn(product, promotions))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> productService.getProductAmount(request))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(UNMATCHED_PROMOTION.getMessage());
    }

    @Nested
    @DisplayName("프로모션 타입 검증")
    class UnmatchedType {
        static Stream<Arguments> provideTypes() {
            return Stream.of(
                    Arguments.of(PromotionType.CODE, DiscountType.WON),
                    Arguments.of(PromotionType.COUPON, DiscountType.PERCENT)
            );
        }

        @ParameterizedTest
        @MethodSource("provideTypes")
        @DisplayName("실패 - 프로모션 타입과 연관되지 않은 할인 타입이면 예외가 발생한다.")
        void getProductAmount_unmatched_type(PromotionType promotionType,
                                             DiscountType discountType) {
            // given
            Product product = Product.builder()
                    .name("피팅노드상품")
                    .price(215000)
                    .build();

            Promotion promotion = Promotion.builder()
                    .promotionType(promotionType)
                    .name("15% 할인코드")
                    .discountType(discountType)
                    .discountValue(15)
                    .useStartedAt(LocalDate.now())
                    .useEndedAt(LocalDate.now().plusMonths(1))
                    .build();

            List<Promotion> promotions = List.of(promotion);

            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));

            given(promotionRepository.findByIdIn(couponIds))
                    .willReturn(promotions);

            given(promotionProductsRepository
                    .existsByProductAndPromotionIn(product, promotions))
                    .willReturn(true);

            // when
            // then
            assertThatThrownBy(() -> productService.getProductAmount(request))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(INTERNAL_ERROR.getMessage());
        }
    }

    @Test
    @DisplayName("실패 - 가격보다 할인 가격이 더 크면 예외가 발생한다.")
    void getProductAmount_invalid_discountValue() {
        // given
        Promotion promotion = Promotion.builder()
                .promotionType(PromotionType.COUPON)
                .name("할인쿠폰")
                .discountType(DiscountType.WON)
                .discountValue(300000)
                .useStartedAt(LocalDate.now())
                .useEndedAt(LocalDate.now().plusMonths(1))
                .build();

        List<Promotion> promotions = List.of(promotion);

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));

        given(promotionRepository.findByIdIn(couponIds))
                .willReturn(promotions);

        given(promotionProductsRepository
                .existsByProductAndPromotionIn(product, promotions))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> productService.getProductAmount(request))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining(INVALID_DISCOUNT_PRICE.getMessage());
    }
}