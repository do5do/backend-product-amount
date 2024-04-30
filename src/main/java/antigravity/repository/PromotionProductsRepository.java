package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionProductsRepository
        extends JpaRepository<PromotionProducts, Long> {

    boolean existsByProductAndPromotionIn(Product product, List<Promotion> promotions);
}
