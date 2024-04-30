package antigravity.domain.entity;

import antigravity.domain.common.BaseTimeEntity;
import antigravity.exception.ProductException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static antigravity.exception.ErrorCode.INVALID_PRODUCT_PRICE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Product extends BaseTimeEntity {
    private static final Integer MIN_PRICE = 10_000;
    private static final Integer MAX_PRICE = 10_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Builder
    public Product(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public void validatePrice() {
        if (price < MIN_PRICE || price > MAX_PRICE) {
            throw new ProductException(INVALID_PRODUCT_PRICE);
        }
    }
}
