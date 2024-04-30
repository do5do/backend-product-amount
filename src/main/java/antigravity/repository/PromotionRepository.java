package antigravity.repository;

import antigravity.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByIdIn(List<Long> ids);
}
